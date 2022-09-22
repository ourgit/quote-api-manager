package controllers.basic;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.BaseAdminSecurityController;
import io.ebean.DB;
import models.admin.AdminMember;
import models.admin.GroupAction;
import play.db.ebean.Transactional;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import services.AppInit;
import utils.ValidationUtil;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * 组里权限控制器
 */
public class GroupActionController extends BaseAdminSecurityController {
    @Inject
    AppInit appInit;

    /**
     * @api {POST} /v1/cp/group_action/ 06修改组的权限
     * @apiName updateGroupAction
     * @apiGroup Admin-GROUP
     * @apiParam {int} groupId 组id
     * @apiParam {string} actionId 权限id，多个权限以半角逗号(,)进行分隔
     * @apiSuccess (Success 40001) {int} code 40001 参数错误
     * @apiSuccess (Success 40002) {int} code 40002 该组已拥有该权限
     * @apiSuccess (Success 200) {int} code 200 请求成功
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> updateGroupAction(Http.Request request) {
        JsonNode node = request.body().asJson();
        Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
        return CompletableFuture.supplyAsync(() -> {
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            int groupId = node.findPath("groupId").asInt();
            String actionId = node.findPath("actionId").asText();
            if (groupId < 1 || ValidationUtil.isEmpty(actionId)) return okCustomJson(CODE40001, "参数错误");
            List<GroupAction> groupActionList = GroupAction.find.query().where().eq("groupId", groupId).findList();
            if (groupActionList.size() > 0) DB.deleteAll(groupActionList);
            String[] actionIds = actionId.split(",");
            List<GroupAction> actions = new ArrayList<>();
            for (String eachActionId : actionIds) {
                GroupAction newGroupAction = new GroupAction();
                newGroupAction.setGroupId(groupId);
                newGroupAction.setActionId(eachActionId);
                actions.add(newGroupAction);
            }
            if (actions.size() > 0) DB.saveAll(actions);
            updateCache();
            businessUtils.addOperationLog(request, admin, "修改组的权限：" + actions.toString());
            return okJSON200();
        });
    }

    private void updateCache() {
        appInit.saveAdminCache();
    }
}
