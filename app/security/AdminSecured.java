package security;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.admin.AdminMember;
import models.system.ParamConfig;
import play.Logger;
import play.api.routing.HandlerDef;
import play.cache.AsyncCacheApi;
import play.cache.NamedCache;
import play.cache.SyncCacheApi;
import play.libs.Json;
import play.libs.typedmap.TypedKey;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import play.routing.Router;
import services.AppInit;
import utils.BizUtils;
import utils.CacheUtils;
import utils.EncodeUtils;
import utils.ValidationUtil;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static constants.BusinessConstant.PARAM_KEY_TIME_STAMP_INSTANCE;
import static constants.RedisKeyConstant.ADMIN_GROUP_ACTION_KEY_SET;
import static constants.RedisKeyConstant.ADMIN_KEY_MEMBER_ID_AUTH_TOKEN_PREFIX;


public class AdminSecured extends Security.Authenticator {
    play.Logger.ALogger logger = Logger.of(AdminSecured.class);

    public static final String WITHOUT_THIS_AUTH_STRING = "WITHOUT_THIS_AUTH_STRING";
    public static final String ACTION_METHOD = "ACTION_METHOD";
    public static final TypedKey<String> WITHOUT_THIS_AUTH = TypedKey.create(WITHOUT_THIS_AUTH_STRING);
    @Inject
    BizUtils businessUtils;
    @Inject
    CacheUtils cacheUtils;

    @Inject
    @NamedCache("redis")
    protected AsyncCacheApi redis;

    @Inject
    EncodeUtils encodeUtils;

    @Inject
    AppInit appInit;


    /**
     * 认证接口，目前以token作为凭据，
     * 根据token值从缓存中取出组id,如果该组不存在，返回没有权限使用
     * 如果该组中不包含该请求的action,返回没有权限使用.
     * 如果达到以上两点，说明是有权限使用该功能，验证通过。
     *
     * @return
     */
    @Override
    public Optional<String> getUsername(Http.Request request) {
        Optional<String> set = redis.sync().get(ADMIN_GROUP_ACTION_KEY_SET);
        if (!set.isPresent()) appInit.saveAdminCache();
        if (ValidationUtil.isEmpty(set.get())) appInit.saveAdminCache();

        Optional<AdminMember> optional = businessUtils.getAdminByAuthToken(request);
        AdminMember adminMember = null;
        if (optional.isPresent()) adminMember = optional.get();
        if (null == adminMember) return Optional.empty();
        List<Integer> groupIdList = adminMember.groupIdList;
        if (groupIdList.size() < 1) return Optional.empty();
        String host = request.host();
//        String hostHashValue = encodeUtils.getMd5WithSalt(host);
//        String correctHash = getHostHash();
//        if (!hostHashValue.equalsIgnoreCase(correctHash)) return Optional.empty();
        Optional<HandlerDef> handlerDef = request.attrs().getOptional(Router.Attrs.HANDLER_DEF);
        if (handlerDef.isPresent()) {
            HandlerDef def = handlerDef.get();
            String actionMethod = def.method();
            for (Integer groupId : groupIdList) {
                String actionKey = cacheUtils.getGroupActionKey(groupId);
                Optional<Set<String>> actionOptional = redis.sync().get(actionKey);
                if (!actionOptional.isPresent()) return Optional.empty();
                if (ValidationUtil.isEmpty(actionMethod)) return Optional.empty();
                String actionHashValue = encodeUtils.getMd5WithSalt(actionMethod);
                Set<String> groupActions = actionOptional.get();
                if (null != groupActions && groupActions.contains(actionHashValue)) {
                    refreshToken(adminMember, request);
                    return Optional.of(adminMember.realName);
                }
            }
            logger.info("action not match");
            request.getHeaders().adding(WITHOUT_THIS_AUTH_STRING, WITHOUT_THIS_AUTH_STRING);
            request.getHeaders().adding(ACTION_METHOD, actionMethod);
            return Optional.empty();
        }
        return Optional.empty();
    }

    private void refreshToken(AdminMember member, Http.Request request) {
        String uidToken = businessUtils.getUIDFromRequest(request);
        if (!ValidationUtil.isEmpty(uidToken)) {
            String key = ADMIN_KEY_MEMBER_ID_AUTH_TOKEN_PREFIX + member.id;
            int expireTime = businessUtils.getTokenExpireTime();
            Optional<String> optional = redis.sync().get(key);
            if (optional.isPresent()) {
                String token = optional.get();
                redis.set(key, token, expireTime);
                redis.set(token, member, expireTime);
            }
            redis.set(uidToken, member.id + "", expireTime);
        }

    }

    @Override
    public Result onUnauthorized(Http.Request request) {
        ObjectNode node = Json.newObject();
        node.put("code", 403);
        node.put("reason", "亲，请先登录 ");
        Optional<String> sessionOptional = request.getHeaders().get(WITHOUT_THIS_AUTH_STRING);
        if (sessionOptional.isPresent()) {
            node.put("code", 40003);
            String method = "";
            Optional<String> methodOptional = request.getHeaders().get(ACTION_METHOD);
            if (methodOptional.isPresent()) {
                method = methodOptional.get();
            }
            node.put("reason", "没有权限使用该功能:" + method);
            request.getHeaders().removing(WITHOUT_THIS_AUTH_STRING);
        }

        return ok(node);
    }

    public String getHostHash() {
        String value = "";
        Optional<String> accountOptional = redis.sync().get(PARAM_KEY_TIME_STAMP_INSTANCE);
        if (accountOptional.isPresent()) {
            value = accountOptional.get();
            if (!ValidationUtil.isEmpty(value)) {
                return value;
            }
        }
        if (ValidationUtil.isEmpty(value)) {
            ParamConfig config = ParamConfig.find.query().where().eq("key", PARAM_KEY_TIME_STAMP_INSTANCE)
                    .setMaxRows(1).findOne();
            if (null != config) {
                value = config.value;
                redis.set(PARAM_KEY_TIME_STAMP_INSTANCE, value);
            }
        }
        return value;
    }

}
