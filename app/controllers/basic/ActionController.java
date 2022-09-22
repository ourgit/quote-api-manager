package controllers.basic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.BaseAdminSecurityController;
import io.ebean.DB;
import io.ebean.SqlRow;
import models.admin.Action;
import models.admin.AdminMember;
import models.admin.GroupAction;
import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import services.AppInit;
import utils.EncodeUtils;
import utils.ValidationUtil;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static constants.RedisKeyConstant.ADMIN_GROUP_ACTION_KEY_SET;

/**
 * action控制器
 */
public class ActionController extends BaseAdminSecurityController {

    @Inject
    AppInit appInit;

    @Inject
    EncodeUtils encodeUtils;

    /**
     * @api {GET} /v1/cp/action/:actionId/ 01权限详情
     * @apiName getAction
     * @apiGroup Admin-Action
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200) {string} id actionId
     * @apiSuccess (Success 200) {string} actionName 权限名称，对应系统内部一个控制器
     * @apiSuccess (Success 200) {string} actionDesc 权限描述，中文说明
     * @apiSuccess (Success 200) {string} moduleName 权限名称，英文简写
     * @apiSuccess (Success 200) {string} moduleDesc 模块名称，中文说明
     * @apiSuccess (Success 200) {boolean} needShow 没有该模块的权限，模块是否显示，默认为false不显示
     * @apiSuccess (Success 200) {int} sortValue 排序值
     * @apiSuccess (Success 200) {long} createdTime 创建时间
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     * @apiSuccess (Error 40002) {int} code 40002 该权限不存在
     */
    public CompletionStage<Result> getAction(String actionId) {
        return CompletableFuture.supplyAsync(() -> {
            if (ValidationUtil.isEmpty(actionId)) return okCustomJson(CODE40001, "参数错误");
            Action action = Action.find.byId(actionId);
            if (null == action) return okCustomJson(CODE40002, "该权限不存在");
            ObjectNode result = (ObjectNode) Json.toJson(action);
            result.put("code", 200);
            return ok(result);
        });
    }

    /**
     * @api {GET} /v1/cp/actions/ 02权限列表
     * @apiName listActions
     * @apiGroup Admin-Action
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200) {json} list
     * @apiSuccess (Success 200) {string} id actionId
     * @apiSuccess (Success 200) {string} actionName 权限名称，对应系统内部一个控制器
     * @apiSuccess (Success 200) {string} actionDesc 权限描述，中文说明
     * @apiSuccess (Success 200) {string} moduleName 权限名称，英文简写
     * @apiSuccess (Success 200) {string} moduleDesc 模块名称，中文说明
     * @apiSuccess (Success 200)  {boolean} needShow 没有该模块的权限，模块是否显示，默认为false不显示
     * @apiSuccess (Success 200) {int} sortValue 排序值
     * @apiSuccess (Success 200) {long} createdTime 创建时间
     */
    public CompletionStage<Result> listActions() {
        return CompletableFuture.supplyAsync(() -> {
            List<Action> list = Action.find.all();
            Map<String, ArrayNode> module = new HashMap<>();
            for (Action action : list) {
                String moduleName = action.moduleName;
                ArrayNode actionsByModule = module.get(moduleName);
                if (null == actionsByModule) {
                    actionsByModule = Json.newArray();
                    module.put(moduleName, actionsByModule);
                }
                ObjectNode node = (ObjectNode) Json.toJson(action);
                actionsByModule.add(node);
            }
            ObjectNode result = Json.newObject();
            module.forEach((moduleName, arrayNode) -> result.set(moduleName, arrayNode));
            result.put("code", 200);
            return ok(result);
        });
    }


    /**
     * @api {POST} /v1/cp/action/new/ 03新建权限
     * @apiName addAction
     * @apiGroup Admin-Action
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiParam {string} actionName 权限名称，对应系统内部一个控制器
     * @apiParam {string} actionDesc 权限描述，中文说明
     * @apiParam {string} moduleName 权限名称，英文简写
     * @apiParam {string} moduleDesc 模块名称，中文说明
     * @apiParam {boolean} needShow 没有该模块的权限，模块是否显示，默认为false不显示
     * @apiParam {int} sortValue 排序值
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     * @apiSuccess (Error 40002) {int} code 40002 该权限已存在
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> addAction(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
        return CompletableFuture.supplyAsync(() -> {
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            Action action = Json.fromJson(jsonNode, Action.class);
            if (ValidationUtil.isEmpty(action.actionName)
                    || ValidationUtil.isEmpty(action.actionDesc)
                    || ValidationUtil.isEmpty(action.actionDesc)
                    || ValidationUtil.isEmpty(action.moduleName)
            ) return okCustomJson(CODE40001, "参数错误");
            Action existAction = Action.find.query().where().eq("actionName", action.actionName).findOne();
            if (null != existAction) return okCustomJson(CODE40002, "该权限已存在");
            String actionId = encodeUtils.getMd5WithSalt(action.actionName);
            action.setId(actionId);
            action.setCreatedTime(dateUtils.getCurrentTimeBySecond());
            action.save();
            GroupAction groupAction = new GroupAction();
            groupAction.setActionId(actionId);
            groupAction.setGroupId(1);
            groupAction.save();
            updateCache();
            businessUtils.addOperationLog(request, admin, "添加权限:" + action.toString());
            return okJSON200();
        });
    }


    /**
     * @api {POST} /v1/cp/action/:id/ 04修改权限
     * @apiName updateAction
     * @apiGroup Admin-Action
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiParam {string} actionName 权限名称，对应系统内部一个控制器
     * @apiParam {string} actionDesc 权限描述，中文说明
     * @apiParam {string} moduleName 权限名称，英文简写
     * @apiParam {string} moduleDesc 模块名称，中文说明
     * @apiParam {boolean} needShow 没有该模块的权限，模块是否显示，该值必须传，如果不传会导致使用默认的false写入库里
     * @apiParam {int} sortValue 排序值
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     * @apiSuccess (Error 40001) {int} code 40002 分组不存在
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> updateAction(Http.Request request, String id) {
        return CompletableFuture.supplyAsync(() -> {
            JsonNode node = request.body().asJson();
            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            if (null == node || ValidationUtil.isEmpty(id)) return okCustomJson(CODE40001, "参数错误");
            Action param = Json.fromJson(node, Action.class);
            if (null == param) return okCustomJson(CODE40001, "参数错误");
            Action existAction = Action.find.byId(id);
            if (null == existAction) return okCustomJson(CODE40002, "权限不存在");
            businessUtils.addOperationLog(request, admin, "修改权限，执行前" + existAction.toString() + ";修改的参数：" + param.toString());
            if (!ValidationUtil.isEmpty(param.actionDesc) && !param.actionDesc.equals(existAction.actionDesc)) existAction.setActionDesc(param.actionDesc);
            if (!ValidationUtil.isEmpty(param.moduleName) && !param.moduleName.equals(existAction.moduleName)) existAction.setModuleName(param.moduleName);
            if (!ValidationUtil.isEmpty(param.moduleDesc) && !param.moduleDesc.equals(existAction.moduleDesc)) existAction.setModuleDesc(param.moduleDesc);
            existAction.setNeedShow(param.needShow);
            if (param.sortValue > 0) existAction.setSortValue(param.sortValue);
            if (!ValidationUtil.isEmpty(param.actionName) && !param.actionName.equals(existAction.actionName)) {
                Action newAction = new Action();
                newAction.setActionName(param.actionName);
                String actionId = encodeUtils.getMd5WithSalt(param.actionName);
                newAction.setId(actionId);
                newAction.setActionDesc(existAction.actionDesc);
                newAction.setModuleDesc(existAction.moduleDesc);
                newAction.setModuleName(existAction.moduleName);
                newAction.setNeedShow(true);
                newAction.setSortValue(existAction.sortValue);
                newAction.setCreatedTime(dateUtils.getCurrentTimeBySecond());
                newAction.insert();
                existAction.delete();

                List<GroupAction> list = GroupAction.find.query().where().eq("actionId", id).findList();
                if (list.size() > 0) {
                    list.forEach((each) -> each.setActionId(actionId));
                    DB.saveAll(list);
                }
            } else existAction.save();
            updateCache();
            return okJSON200();
        });
    }

    private void updateCache() {
        syncCache.remove(ADMIN_GROUP_ACTION_KEY_SET);
        appInit.saveAdminCache();
    }

    /**
     * @api {POST} /v1/cp/action/ 05删除权限
     * @apiName delAction
     * @apiGroup Admin-Action
     * @apiParam {String} id 权限id
     * @apiParam {String} operation 操作,"del"为删除
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     * @apiSuccess (Error 40001) {int} code 40002 分组不存在
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> delAction(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
        return CompletableFuture.supplyAsync(() -> {
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            String operation = jsonNode.findPath("operation").asText();
            if (ValidationUtil.isEmpty(operation) || !operation.equals("del")) return okCustomJson(CODE40001, "参数错误");
            String id = jsonNode.findPath("id").asText();
            if (ValidationUtil.isEmpty(id)) return okCustomJson(CODE40001, "参数错误");
            Action action = Action.find.byId(id);
            if (null == action) return okCustomJson(CODE40002, "分组不存在");
            businessUtils.addOperationLog(request, admin, "删除权限：" + action.toString());
            action.delete();
            updateCache();
            return okJSON200();
        });
    }

    /**
     * @api {GET} /v1/cp/actions_by_filter/:groupId/ 06根据groupId取出权限
     * @apiName getGroupActionByGroupId
     * @apiGroup Admin-Action
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200) {string} id actionId
     * @apiSuccess (Success 200) {string} actionName 权限名称，对应系统内部一个控制器
     * @apiSuccess (Success 200) {string} actionDesc 权限描述，中文说明
     * @apiSuccess (Success 200) {string} moduleName 权限名称，英文简写
     * @apiSuccess (Success 200) {string} moduleDesc 模块名称，中文说明
     * @apiSuccess (Success 200) {boolean} needShow 没有该模块的权限，模块是否显示，默认为false不显示
     * @apiSuccess (Success 200) {int} sortValue 排序值
     * @apiSuccess (Success 200) {long} createdTime 创建时间
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     */
    public CompletionStage<Result> getGroupActionByGroupId(int groupId) {
        return CompletableFuture.supplyAsync(() -> {
            if (groupId < 1) return okCustomJson(CODE40001, "参数错误");
            String sql = "SELECT *,cp_system_action.id as actionId FROM cp_system_action,cp_group_action " +
                    "WHERE cp_system_action.id = cp_group_action.`system_action_id` AND cp_group_action.`group_id` = :groupId ";
            List<SqlRow> list = DB.createSqlQuery(sql).setParameter("groupId", groupId).findList();
            ArrayNode nodes = Json.newArray();
            list.forEach((sqlRow) -> {
                ObjectNode node = Json.newObject();
                node.put("id", sqlRow.getString("actionId"));
                node.put("actionName", sqlRow.getString("action_name"));
                node.put("actionDesc", sqlRow.getString("action_desc"));
                node.put("moduleName", sqlRow.getString("module_name"));
                node.put("moduleDesc", sqlRow.getString("module_desc"));
                node.put("needShow", sqlRow.getBoolean("need_show"));
                node.put("sortValue", sqlRow.getInteger("display_order"));
                nodes.add(node);
            });
            ObjectNode result = Json.newObject();
            result.set("list", nodes);
            result.put(CODE, CODE200);
            return ok(result);
        });
    }

}
