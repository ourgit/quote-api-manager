package controllers.basic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import constants.BusinessConstant;
import controllers.BaseAdminSecurityController;
import io.ebean.PagedList;
import models.upgrade.GreyUser;
import models.upgrade.UpgradeConfig;
import models.user.Member;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import utils.ValidationUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * OTC管理
 */
public class UpdateManager extends BaseAdminSecurityController {

    /**
     * @api {GET} /v1/cp/update_configs/  01升级配置列表
     * @apiName listUpdateConfigs
     * @apiGroup ADMIN-UPDATE
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess {JsonArray} list
     * @apiSuccess {long} id id
     * @apiSuccess {string} name 配置名字
     * @apiSuccess {int} versionNumber 当前版本号
     * @apiSuccess {boolean} enable 是否启用
     * @apiSuccess {String} forceUpdateRule 强制更新的规则，支持 >=,>,<=,<,&&
     * @apiSuccess {String} normalUpdateRule 普通更新的规则，支持 >=,>,<=,<,&&
     * @apiSuccess {String} updateUrl 升级地址
     * @apiSuccess {int} updateType  1热更新 2整包更新
     * @apiSuccess {int} versionType 版本类型 1正式版本，2灰度版本
     * @apiSuccess {String} note 备注
     * @apiSuccess {String} createTime 时间
     */
    public CompletionStage<Result> listUpdateConfigs(int page) {
        return CompletableFuture.supplyAsync(() -> {
            PagedList<UpgradeConfig> pagedList = UpgradeConfig.find.query().where().orderBy().desc("id")
                    .setFirstRow((page - 1) * BusinessConstant.PAGE_SIZE_10)
                    .setMaxRows(BusinessConstant.PAGE_SIZE_10)
                    .findPagedList();
            int pages = pagedList.getTotalPageCount();
            List<UpgradeConfig> list = pagedList.getList();
            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            result.put("pages", pages);
            result.set("list", Json.toJson(list));
            return ok(result);
        });
    }

    /**
     * @api {GET} /v1/cp/update_configs/:id/ 02升级配置详情
     * @apiName getUpdateConfig
     * @apiGroup ADMIN-UPDATE
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess {long} id id
     * @apiSuccess {string} name 配置名字
     * @apiSuccess {int} versionNumber 当前版本号
     * @apiSuccess {boolean} enable 是否启用
     * @apiSuccess {String} forceUpdateRule 强制更新的规则，支持 >=,>,<=,<,&&
     * @apiSuccess {String} normalUpdateRule 普通更新的规则，支持 >=,>,<=,<,&&
     * @apiSuccess {String} updateUrl 升级地址
     * @apiSuccess {int} updateType  1热更新 2整包更新
     * @apiSuccess {int} versionType 版本类型 1正式版本，2灰度版本
     * @apiSuccess {String} note 备注
     * @apiSuccess {String} createTime 时间
     */
    public CompletionStage<Result> getUpdateConfig(long id) {
        return CompletableFuture.supplyAsync(() -> {
            UpgradeConfig upgradeConfig = UpgradeConfig.find.byId(id);
            if (null == upgradeConfig) return okCustomJson(CODE40001, "该升级配置不存在");
            ObjectNode resultNode = (ObjectNode) Json.toJson(upgradeConfig);
            resultNode.put(CODE, CODE200);
            return ok(resultNode);
        });
    }

    /**
     * @api {POST} /v1/cp/update_config/new/ 03添加升级配置
     * @apiName addUpdateConfig
     * @apiGroup ADMIN-UPDATE
     * @apiParam {string} name 配置名字
     * @apiParam {int} versionNumber 当前版本号
     * @apiParam {boolean} enable 是否启用
     * @apiParam {String} [forceUpdateRule] 强制更新的规则，支持 >=,>,<=,<,&&
     * @apiParam {String} [normalUpdateRule] 普通更新的规则，支持 >=,>,<=,<,&&
     * @apiParam {String} updateUrl 升级地址
     * @apiParam {int} updateType  1热更新 2整包更新
     * @apiParam {int} versionType 版本类型 1正式版本，2灰度版本
     * @apiParam {String} [note] 备注
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> addUpdateConfig(Http.Request request) {
        return CompletableFuture.supplyAsync(() -> {
            JsonNode requestNode = request.body().asJson();
            if (null == requestNode) return okCustomJson(CODE40001, "参数错误");
            UpgradeConfig param = Json.fromJson(requestNode, UpgradeConfig.class);
            if (null == param) return okCustomJson(CODE40001, "参数错误");
            if (ValidationUtil.isEmpty(param.name)) return okCustomJson(CODE40001, "请输入配置名字");
            if (param.versionNumber < 1) return okCustomJson(CODE40001, "请输入当前版本");
            if (ValidationUtil.isEmpty(param.updateUrl)) return okCustomJson(CODE40001, "请输入升级地址");
            if (ValidationUtil.isEmpty(param.platform)) return okCustomJson(CODE40001, "请输入app平台");
            if (ValidationUtil.isEmpty(param.normalUpdateRule) && ValidationUtil.isEmpty(param.forceUpdateRule)) {
                return okCustomJson(CODE40001, "普通更新规则跟强制更新规则两个必填一个");
            }
            if (param.versionType < 1) return okCustomJson(CODE40001, "请输入版本类型");
            if (param.updateType < 1) return okCustomJson(CODE40001, "update type error");
            if (!requestNode.has("enable")) param.enable = true;
            long currentTime = dateUtils.getCurrentTimeBySecond();
            param.setUpdateTime(currentTime);
            param.setCreateTime(currentTime);
            param.save();
            return okJSON200();
        });
    }


    /**
     * @api {POST} /v1/cp/update_config/:id/ 04修改升级配置
     * @apiName updateConfig
     * @apiGroup ADMIN-UPDATE
     * @apiParam {string} name 配置名字
     * @apiParam {int} versionNumber 当前版本号
     * @apiParam {boolean} enable 是否启用
     * @apiParam {String} [forceUpdateRule] 强制更新的规则，支持 >=,>,<=,<,&&
     * @apiParam {String} [normalUpdateRule] 普通更新的规则，支持 >=,>,<=,<,&&
     * @apiParam {String} updateUrl 升级地址
     * @apiParam {int} updateType  1热更新 2整包更新
     * @apiParam {int} versionType 版本类型 1正式版本，2灰度版本
     * @apiParam {String} [note] 备注
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> updateConfig(Http.Request request, long id) {
        return CompletableFuture.supplyAsync(() -> {
            JsonNode requestNode = request.body().asJson();
            if (null == requestNode) return okCustomJson(CODE40001, "参数错误");
            UpgradeConfig upgradeConfig = UpgradeConfig.find.byId(id);
            if (null == upgradeConfig) return okCustomJson(CODE40002, "该升级配置不存在");
            UpgradeConfig param = Json.fromJson(requestNode, UpgradeConfig.class);
            if (null == param) return okCustomJson(CODE40001, "参数错误");
            if (requestNode.has("enable")) {
                upgradeConfig.setEnable(param.enable);
            }
            if (!ValidationUtil.isEmpty(param.name)) upgradeConfig.setName(param.name);
            if (requestNode.has("forceUpdateRule")) upgradeConfig.setForceUpdateRule(param.forceUpdateRule);
            if (requestNode.has("normalUpdateRule")) upgradeConfig.setNormalUpdateRule(param.normalUpdateRule);
            if (requestNode.has("updateUrl")) upgradeConfig.setUpdateUrl(param.updateUrl);
            if (requestNode.has("note")) upgradeConfig.setNote(param.note);
            if (requestNode.has("platform")) upgradeConfig.setPlatform(param.platform);

            if (param.versionType > 0) upgradeConfig.setVersionType(param.versionType);
            if (param.updateType > 0) upgradeConfig.setUpdateType(param.updateType);
            if (param.versionNumber > 0) upgradeConfig.setVersionNumber(param.versionNumber);
            if (param.tipType > 0) upgradeConfig.setTipType(param.tipType);

            upgradeConfig.setUpdateTime(dateUtils.getCurrentTimeBySecond());
            upgradeConfig.save();
            return okJSON200();
        });
    }

    /**
     * @api {POST} /v1/cp/update_config/ 05删除升级配置
     * @apiName delConfig
     * @apiGroup ADMIN-UPDATE
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiParam {int} id id
     * @apiParam {String} operation 操作,"del"为删除
     * @apiSuccess (Success 40001) {int} code 40001 参数错误
     * @apiSuccess (Success 40002) {int} code 40002 该升级配置不存在
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> delUpdateConfig(Http.Request request) {
        return CompletableFuture.supplyAsync(() -> {
            JsonNode jsonNode = request.body().asJson();
            String operation = jsonNode.findPath("operation").asText();
            if (ValidationUtil.isEmpty(operation) || !operation.equals("del")) return okCustomJson(CODE40001, "参数错误");
            long id = jsonNode.findPath("id").asLong();
            if (id < 1) return okCustomJson(CODE40001, "参数错误");
            UpgradeConfig upgradeConfig = UpgradeConfig.find.byId(id);
            if (null == upgradeConfig) return okCustomJson(CODE40002, "该升级配置不存在");
            upgradeConfig.delete();
            return okJSON200();
        });
    }

    /**
     * @api {GET} /v1/cp/grey_users/  06灰度用户列表
     * @apiName listGreyUsers
     * @apiGroup ADMIN-UPDATE
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess {JsonArray} list
     * @apiSuccess {long} id id
     * @apiSuccess {string} userName 用户名字
     * @apiSuccess {string} note 备注
     * @apiSuccess {long} uid 用户uid
     * @apiSuccess {String} createTime 时间
     */
    public CompletionStage<Result> listGreyUsers(int page) {
        return CompletableFuture.supplyAsync(() -> {
            PagedList<GreyUser> pagedList = GreyUser.find.query().where().orderBy().desc("id")
                    .setFirstRow((page - 1) * BusinessConstant.PAGE_SIZE_10)
                    .setMaxRows(BusinessConstant.PAGE_SIZE_10)
                    .findPagedList();
            int pages = pagedList.getTotalPageCount();
            List<GreyUser> list = pagedList.getList();
            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            result.put("pages", pages);
            result.set("list", Json.toJson(list));
            return ok(result);
        });
    }

    /**
     * @api {GET} /v1/cp/grey_users/:id/ 07灰度用户详情
     * @apiName getGreyUser
     * @apiGroup ADMIN-UPDATE
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess {long} id id
     * @apiSuccess {string} userName 用户名字
     * @apiSuccess {string} note 备注
     * @apiSuccess {long} uid 用户uid
     * @apiSuccess {String} createTime 时间
     */
    public CompletionStage<Result> getGreyUser(long id) {
        return CompletableFuture.supplyAsync(() -> {
            GreyUser upgradeConfig = GreyUser.find.byId(id);
            if (null == upgradeConfig) return okCustomJson(CODE40001, "该灰度用户不存在");
            ObjectNode resultNode = (ObjectNode) Json.toJson(upgradeConfig);
            resultNode.put(CODE, CODE200);
            return ok(resultNode);
        });
    }

    /**
     * @api {POST} /v1/cp/grey_users/new/ 08添加灰度用户
     * @apiName addGreyUser
     * @apiGroup ADMIN-UPDATE
     * @apiSuccess {long} uid 用户ID
     * @apiSuccess {String} [note] 备注
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> addGreyUser(Http.Request request) {
        return CompletableFuture.supplyAsync(() -> {
            JsonNode requestNode = request.body().asJson();
            if (null == requestNode) return okCustomJson(CODE40001, "参数错误");
            GreyUser param = Json.fromJson(requestNode, GreyUser.class);
            if (null == param) return okCustomJson(CODE40001, "参数错误");
            if (param.uid < 1) return okCustomJson(CODE40001, "请选择灰度用户");
            Member member = Member.find.byId(param.uid);
            if (null == member) return okCustomJson(CODE40001, "该用户不存在");
            GreyUser exist = GreyUser.find.query().where().eq("uid", param.uid).findOne();
            if (null != exist) return okCustomJson(CODE40002, "该灰度用户已存在");

            String userName = member.realName;
            if (ValidationUtil.isEmpty(userName)) userName = member.nickName;
            if (ValidationUtil.isEmpty(userName)) userName = member.contactNumber;
            param.setUserName(userName);
            long currentTime = dateUtils.getCurrentTimeBySecond();
            param.setUpdateTime(currentTime);
            param.setCreateTime(currentTime);
            param.save();
            return okJSON200();
        });
    }

    /**
     * @api {POST} /v1/cp/grey_users/ 09删除灰度用户
     * @apiName delGreyUser
     * @apiGroup ADMIN-UPDATE
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiParam {int} id id
     * @apiParam {String} operation 操作,"del"为删除
     * @apiSuccess (Success 40001) {int} code 40001 参数错误
     * @apiSuccess (Success 40002) {int} code 40002 该升级配置不存在
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> delGreyUser(Http.Request request) {
        return CompletableFuture.supplyAsync(() -> {
            JsonNode jsonNode = request.body().asJson();
            String operation = jsonNode.findPath("operation").asText();
            if (ValidationUtil.isEmpty(operation) || !operation.equals("del")) return okCustomJson(CODE40001, "参数错误");
            long id = jsonNode.findPath("id").asLong();
            if (id < 1) return okCustomJson(CODE40001, "参数错误");
            GreyUser greyUser = GreyUser.find.byId(id);
            if (null == greyUser) return okCustomJson(CODE40002, "该灰度用户不存在");
            greyUser.delete();
            return okJSON200();
        });
    }
}
