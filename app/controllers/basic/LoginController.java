package controllers.basic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.BaseAdminController;
import io.ebean.DB;
import models.admin.*;
import play.Logger;
import play.cache.AsyncCacheApi;
import play.cache.NamedCache;
import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import utils.EncodeUtils;
import utils.ValidationUtil;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static constants.RedisKeyConstant.*;

/**
 * 管理员登录控制器
 */
public class LoginController extends BaseAdminController {
    Logger.ALogger logger = Logger.of(LoginController.class);
    @Inject
    EncodeUtils encodeUtils;

    @Inject
    @NamedCache("redis")
    protected AsyncCacheApi redis;

    /**
     * @api {POST} /v1/cp/login/ 01登录
     * @apiName login
     * @apiGroup Admin-Authority
     * @apiParam {jsonObject} data json串格式
     * @apiParam {string} username 用户名.
     * @apiParam {string} password 密码, 6位至20位
     * @apiParam {string} vcode 手机验证码
     * @apiSuccess (Success 200){String}  userName 用户名
     * @apiSuccess (Success 200){String}  realName 真名
     * @apiSuccess (Success 200){String}  lastLoginTimeForShow 最后登录时间
     * @apiSuccess (Success 200){String}  lastLoginIP 最后登录ip
     * @apiSuccess (Success 200){long} id 用户id
     * @apiSuccess (Success 200){String} token token
     * @apiSuccess (Success 200){String} groupName 所在组名
     * @apiSuccess (Error 40001) {int} code 40001  参数错误
     * @apiSuccess (Error 40003) {int} code 40003 用户名或密码错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> login(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        String loginIP = businessUtils.getRequestIP(request);
        return CompletableFuture.supplyAsync(() -> {
            if (null == jsonNode) return okCustomJson(CODE40001, "参数错误");
            String userName = jsonNode.findPath("username").asText();
            String password = jsonNode.findPath("password").asText();
            String verificationCode = jsonNode.findPath("vcode").asText();
            if (ValidationUtil.isEmpty(userName) || ValidationUtil.isEmpty(password))
                return okCustomJson(CODE40001, "参数错误");
            //TODO need open in product
//            if (!businessUtils.checkVcode(userName.trim(), verificationCode))
//                return okCustomJson(CODE40002, "无效手机号码/短信验证码");

            AdminMember member = AdminMember.find.query().where()
                    .eq("userName", userName)
                    .eq("password", encodeUtils.getMd5WithSalt(password)).findOne();
            if (null == member) {
                return okCustomJson(CODE40003, "用户名或密码错误");
            }
            if (businessUtils.uptoErrorLimit(request, KEY_LOGIN_MAX_ERROR_BY_ID + member.id, 10)) {
                member.setStatus(AdminMember.STATUS_LOCK);
                member.save();
            }

            if (member.status == AdminMember.STATUS_LOCK) return okCustomJson(CODE40008, "帐号被锁定，请联系管理员");
            redis.remove(KEY_LOGIN_MAX_ERROR_TIMES + loginIP);
            member.setLastLoginIP(loginIP);
            member.setLastLoginTime(dateUtils.getCurrentTimeBySecond());
            member.save();
            ObjectNode result = (ObjectNode) Json.toJson(member);
            result.put("code", 200);
            //保存到缓存中
            String authToken = UUID.randomUUID().toString();
            String idToken = UUID.randomUUID().toString();
            result.put("uid", idToken);
            result.put("token", authToken);
            handleCacheToken(member, authToken, idToken);
            businessUtils.deleteVcodeCache(userName);
            redis.remove(KEY_LOGIN_MAX_ERROR_TIMES + member.id);
            return ok(result);
        });

    }

    /**
     * 根据token保存cache
     *
     * @param member
     * @param authToken
     */
    private void handleCacheToken(AdminMember member, String authToken, String idToken) {
        String key = ADMIN_KEY_MEMBER_ID_AUTH_TOKEN_PREFIX + member.id;
        String idTokenKey = ADMIN_KEY_MEMBER_ID_TOKEN_PREFIX + member.id;
        Optional<String> oldIdTokenOptional = redis.sync().get(idTokenKey);
        if (oldIdTokenOptional.isPresent()) {
            String oldIdToken = oldIdTokenOptional.get();
            if (!ValidationUtil.isEmpty(oldIdToken)) redis.remove(oldIdToken);
        }
        redis.remove(key);
        redis.remove(idTokenKey);

        List<GroupUser> groupUsers = GroupUser.find.query().where()
                .eq("memberId", member.id).orderBy()
                .asc("groupId").findList();
        StringBuilder groupName = new StringBuilder();
        groupUsers.forEach((each) -> {
            if (null != each) {
                member.groupIdList.add(each.groupId);
                groupName.append(each.groupName).append(",");
            }
        });
        if (groupName.length() > 0) member.setGroupName(groupName.substring(0, groupName.length() - 1));
        int expireTime = businessUtils.getTokenExpireTime();
        redis.set(key, authToken, expireTime);
        redis.set(authToken, member, expireTime);
        redis.set(idToken, member.id + "", expireTime);
        redis.set(idTokenKey, idToken, expireTime);
    }

    //
    public Result batchAdd() {
        List<Group> groups = Group.find.all();
        List<Action> actions = Action.find.all();
        List<GroupAction> groupActions = new ArrayList<>();
        for (Group group : groups) {
            for (Action action : actions) {
                if (group.id == 1) {
                    GroupAction groupAction = new GroupAction();
                    groupAction.setGroupId(group.id);
                    groupAction.setActionId(action.id);
                    groupActions.add(groupAction);
                }
            }
        }
        DB.saveAll(groupActions);
        return okJSON200();
    }


    public CompletionStage<Result> getActionHashCode(String action) {
        return CompletableFuture.supplyAsync(() -> {
            String hashCode = encodeUtils.getMd5WithSalt(action);
            ObjectNode node = Json.newObject();
            node.put("code", 200);
            node.put("hashcode", hashCode);
            return ok(node);
        });
    }

    /**
     * @api {POST} /v1/cp/admin_member_password/ 03修改密码
     * @apiName updatePassword
     * @apiGroup Admin-Authority
     * @apiParam {string} oldPassword 旧密码6-20
     * @apiParam {string} newPassword 新密码6-20
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 40001) {int} code 40001 密码错误,6-20位
     * @apiSuccess (Success 40002) {int} code 40002 该管理员不存在
     * @apiSuccess (Success 40004) {int} code 40004 密码错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> updatePassword(Http.Request request) {
        JsonNode node = request.body().asJson();
        Optional<AdminMember> adminMemberOptional = businessUtils.getAdminByAuthToken(request);
        return CompletableFuture.supplyAsync(() -> {
            if (!adminMemberOptional.isPresent()) return unauth403();
            AdminMember member = AdminMember.find.byId(adminMemberOptional.get().id);
            if (null == member) return okCustomJson(CODE40001, "该用户不存在");
            String oldPassword = node.findPath("oldPassword").asText();
            String newPassword = node.findPath("newPassword").asText();
            if (!ValidationUtil.isValidPassword(oldPassword) || !ValidationUtil.isValidPassword(newPassword))
                return okCustomJson(CODE40001, "密码错误,6-20位");
            if (null == member) return okCustomJson(CODE40002, "找不到该用户");
            if (!encodeUtils.getMd5WithSalt(oldPassword).equals(member.password))
                return okCustomJson(CODE40004, "密码错误");
            member.setPassword(encodeUtils.getMd5WithSalt(newPassword));
            member.save();
            return okJSON200();
        });
    }

    /**
     * @api {GET} /v1/cp/is_login/ 04是否已登录
     * @apiName isLogin
     * @apiGroup Admin-Authority
     * @apiSuccess (Success 200){int} code 200
     * @apiSuccess {boolean} login true已登录 false未登录
     * @apiSuccess (Error 40001){int} code 40001 参数错误
     */
    public CompletionStage<Result> isLogin(Http.Request request) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<AdminMember> adminMemberOptional = businessUtils.getAdminByAuthToken(request);

            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            boolean isLogin = false;
            if (adminMemberOptional.isPresent()) {
                AdminMember adminMember = adminMemberOptional.get();
                if (null != adminMember) isLogin = true;
            }
            result.put("login", isLogin);
            return ok(result);
        });
    }
}
