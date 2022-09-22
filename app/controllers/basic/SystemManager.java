package controllers.basic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import constants.BusinessConstant;
import controllers.BaseAdminSecurityController;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import models.admin.AdminMember;
import models.log.OperationLog;
import models.system.ParamConfig;
import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import utils.ValidationUtil;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * 统计管理
 */
public class SystemManager extends BaseAdminSecurityController {

    /**
     * @api {GET} /v1/cp/param_config/?page=&key= 01获取配置列表
     * @apiName listParamConfig
     * @apiGroup ADMIN-System
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200) {int} pages 页数
     * @apiSuccess (Success 200) {JsonArray} list 列表
     * @apiSuccess (Success 200){int} id 配置id
     * @apiSuccess (Success 200){String} key key
     * @apiSuccess (Success 200){String} value 值
     * @apiSuccess (Success 200){String} note 中文备注
     * @apiSuccess (Success 40001) {int} code 40001 参数错误
     * @apiSuccess (Success 40002) {int} code 40002 配置不存在
     * @apiSuccess (Success 40003) {int} code 40003 该配置的KEY已存在
     */
    public CompletionStage<Result> listParamConfig(int page, String key) {
        if (page < 1) page = 1;
        final int queryPage = page - 1;
        return CompletableFuture.supplyAsync(() -> {
            ExpressionList<ParamConfig> expressionList = ParamConfig.find.query().where().eq("enable", true);
            if (!ValidationUtil.isEmpty(key)) expressionList.icontains("key", key);
            PagedList<ParamConfig> pagedList = expressionList.orderBy().desc("id").orderBy().asc("key")
                    .setFirstRow(queryPage * BusinessConstant.PAGE_SIZE_20)
                    .setMaxRows(BusinessConstant.PAGE_SIZE_20)
                    .findPagedList();
            List<ParamConfig> list = pagedList.getList();
            int pages = pagedList.getTotalPageCount();
            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            result.put("pages", pages);
            result.set("list", Json.toJson(list));
            return ok(result);
        });
    }

    /**
     * @api {GET} /v1/cp/param_config/:configId/ 02获取配置详情
     * @apiName getParamConfig
     * @apiGroup ADMIN-System
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200){int} id 配置id
     * @apiSuccess (Success 200){String} key key
     * @apiSuccess (Success 200){String} value 值
     * @apiSuccess (Success 200){String} note 中文备注
     * @apiSuccess (Success 40001) {int} code 40001 参数错误
     * @apiSuccess (Success 40002) {int} code 40002 配置不存在
     * @apiSuccess (Success 40003) {int} code 40003 该配置的KEY已存在
     */
    public CompletionStage<Result> getParamConfig(int configId) {
        return CompletableFuture.supplyAsync(() -> {
            if (configId < 1) return okCustomJson(CODE40001, "参数错误");
            ParamConfig config = ParamConfig.find.query().where().eq("id", configId)
                    .eq("enable", true)
                    .setMaxRows(1)
                    .findOne();
            if (null == config) return okCustomJson(CODE40002, "找不到该配置");
            ObjectNode result = (ObjectNode) Json.toJson(config);
            result.put(CODE, CODE200);
            return ok(result);
        });
    }

    /**
     * @api {POST} /v1/cp/param_config/new/ 03增加配置
     * @apiName addParamConfig
     * @apiGroup ADMIN-System
     * @apiParam {String} key key
     * @apiParam {String} value 值
     * @apiParam {String} note 中文备注
     * @apiSuccess (Success 40001) {int} code 40001 参数错误
     * @apiSuccess (Success 40002) {int} code 40002 配置不存在
     * @apiSuccess (Success 40003) {int} code 40003 该配置的KEY已存在
     * @apiSuccess (Success 200) {int} code 200 请求成功
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> addParamConfig(Http.Request request) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            JsonNode requestNode = request.body().asJson();
            if (null == requestNode) return okCustomJson(CODE40001, "参数错误");
            ParamConfig param = Json.fromJson(requestNode, ParamConfig.class);
            if (null == param) return okCustomJson(CODE40001, "参数错误");
            if (ValidationUtil.isEmpty(param.key) || ValidationUtil.isEmpty(param.value) || ValidationUtil.isEmpty(param.note))
                return okCustomJson(CODE40001, "参数错误");
            ParamConfig config = ParamConfig.find.query().where().eq("key", param.key).setMaxRows(1).findOne();
            if (null != config) return okCustomJson(CODE40003, "该配置的KEY已存在");
            param.setUpdateTime(dateUtils.getCurrentTimeBySecond());
            param.setEnable(true);
            param.save();
            businessUtils.addOperationLog(request, admin, "增加系统参数配置：" + param.toString());
            updateParamConfigCache();
            return okJSON200();
        });
    }

    private void updateParamConfigCache() {
        cacheUtils.updateParamConfigCache();
    }

    /**
     * @api {POST} /v1/cp/param_config/:configId/ 04修改配置
     * @apiName updateParamConfig
     * @apiGroup ADMIN-System
     * @apiParam {String} value 值
     * @apiParam {String} note 中文备注
     * @apiSuccess (Success 40001) {int} code 40001 参数错误
     * @apiSuccess (Success 40002) {int} code 40002 配置不存在
     * @apiSuccess (Success 200) {int} code 200 请求成功
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> updateParamConfig(Http.Request request, int configId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            JsonNode requestNode = request.body().asJson();
            if (null == requestNode || configId < 1) return okCustomJson(CODE40001, "参数错误");
            ParamConfig param = Json.fromJson(requestNode, ParamConfig.class);
            if (null == param) return okCustomJson(CODE40001, "参数错误");

            ParamConfig config = ParamConfig.find.byId(configId);
            if (null == config) return okCustomJson(CODE40002, "该配置不存在");
            businessUtils.addOperationLog(request, admin, "修改系统参数配置，执行前" + config.toString() + ";修改的参数：" + param.toString());

            if (!ValidationUtil.isEmpty(param.key)) {
                ParamConfig existConfig = ParamConfig.find.query().where().eq("key", param.key).setMaxRows(1).findOne();
                if (null != existConfig) return okCustomJson(CODE40001, "该KEY已存在");
                config.setKey(param.key);
            }
            if (!ValidationUtil.isEmpty(param.value)) config.setValue(param.value);
            if (!ValidationUtil.isEmpty(param.note)) config.setNote(param.note);
            config.setUpdateTime(dateUtils.getCurrentTimeBySecond());
            config.save();
            updateParamConfigCache();
            return okJSON200();
        });
    }

    /**
     * @api {POST} /v1/cp/param_config/ 05删除配置
     * @apiName delParamConfig
     * @apiGroup ADMIN-System
     * @apiParam {int} id configId
     * @apiParam {String} operation 操作,"del"为删除
     * @apiSuccess (Success 40001) {int} code 40001 参数错误
     * @apiSuccess (Success 40002) {int} code 40002 配置不存在
     * @apiSuccess (Success 200) {int} code 200 请求成功
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> delParamConfig(Http.Request request) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            JsonNode jsonNode = request.body().asJson();
            String operation = jsonNode.findPath("operation").asText();
            if (ValidationUtil.isEmpty(operation) || !operation.equals("del")) return okCustomJson(CODE40001, "参数错误");
            int id = jsonNode.findPath("id").asInt();
            if (id < 1) return okCustomJson(CODE40001, "参数错误");
            ParamConfig config = ParamConfig.find.byId(id);
            if (null == config) return okCustomJson(CODE40002, "配置不存在");
            businessUtils.addOperationLog(request, admin, "删除系统参数配置：" + config.toString());
            config.delete();
            updateParamConfigCache();
            return okJSON200();
        });
    }
    /**
     * @api {GET} /v1/cp/operation_logs/?page=&key= 06操作日志
     * @apiName listOperationLog
     * @apiGroup ADMIN-System
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200) {int} pages 页数
     * @apiSuccess (Success 200) {JsonArray} list 列表
     * @apiSuccess (Success 200){int} id id
     * @apiSuccess (Success 200){String} adminId 管理员ID
     * @apiSuccess (Success 200){String} adminName 管理员名字
     * @apiSuccess (Success 200){String} ip 操作时IP
     * @apiSuccess (Success 200){String} place ip地址
     * @apiSuccess (Success 200){String} note 操作说明
     * @apiSuccess (Success 200){String} createTime 操作时间
     */
    public CompletionStage<Result> listOperationLog(int page, String adminName, long adminId) {
        if (page < 1) page = 1;
        final int queryPage = page - 1;
        return CompletableFuture.supplyAsync(() -> {
            ExpressionList<OperationLog> expressionList = OperationLog.find.query().where();
            if (!ValidationUtil.isEmpty(adminName)) expressionList.icontains("adminName", adminName);
            if (adminId > 0) expressionList.eq("adminId", adminId);
            PagedList<OperationLog> pagedList = expressionList.orderBy().desc("id")
                    .setFirstRow(queryPage * BusinessConstant.PAGE_SIZE_20)
                    .setMaxRows(BusinessConstant.PAGE_SIZE_20)
                    .findPagedList();
            List<OperationLog> list = pagedList.getList();
            int pages = pagedList.getTotalPageCount();
            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            result.put("pages", pages);
            result.set("list", Json.toJson(list));
            return ok(result);
        });
    }

}
