package controllers.basic;

import controllers.BaseAdminController;
import play.mvc.Http;
import play.mvc.Result;
import utils.ValidationUtil;

import java.util.Optional;

import static constants.RedisKeyConstant.ADMIN_KEY_MEMBER_ID_AUTH_TOKEN_PREFIX;

/**
 * 管理员注销控制器
 */
public class LogoutController extends BaseAdminController {
    /**
     * @api {POST} /v1/cp/logout/ 03注销
     * @apiName logout
     * @apiGroup Admin-Authority
     * @apiSuccess (Success 200){int} code 200 注销成功.
     * @apiSuccess (Error 40003) {int} code 40003 未提供token
     */
    public Result logout(Http.Request request) {
        String idToken = businessUtils.getUIDFromRequest(request);
        if (!ValidationUtil.isEmpty(idToken)) {
            Optional<String> uidOptional = syncCache.getOptional(idToken);
            if(uidOptional.isPresent()){
                String uid = uidOptional.get();
                if(!ValidationUtil.isEmpty(uid)){
                    String key = ADMIN_KEY_MEMBER_ID_AUTH_TOKEN_PREFIX + uid;
                    Optional<String> tokenOptional =  syncCache.getOptional(key);
                    if(tokenOptional.isPresent()){
                        String token = tokenOptional.get();
                        if(!ValidationUtil.isEmpty(token)){
                            syncCache.remove(token);
                        }
                    }
                    syncCache.remove(key);
                }
                syncCache.remove(uid);
            }
            return okJSON200();
        } else return okCustomJson(CODE40003, "无效的用户");
    }

}
