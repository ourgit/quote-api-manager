package controllers.basic;

import com.fasterxml.jackson.databind.JsonNode;
import constants.RedisKeyConstant;
import controllers.BaseController;
import models.admin.AdminMember;
import play.Logger;
import play.cache.NamedCache;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import utils.ValidationUtil;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static constants.RedisKeyConstant.EXIST_REQUEST_SMS;

/**
 *
 */
public class SmsController extends BaseController {
    @Inject
    @NamedCache("redis")
    protected play.cache.AsyncCacheApi redis;

    public static final String SMS_TEMPLATE = "【晴松】本次验证码：**code**，10分钟内有效，请勿泄露。";
    Logger.ALogger logger = Logger.of(SmsController.class);

    /**
     * @api {POST} /v1/cp/request_sms/ 01请求短信验证码
     * @apiName requestVCode
     * @apiGroup Admin-SMS
     * @apiParam {string} phoneNumber 手机号码
     * @apiSuccess (Success 200){int} code 200 成功发送短信
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     * @apiSuccess (Error 40002) {int} code 40002 发送失败
     * @apiSuccess (Error 40003) {int} code 40003 达到限制次数
     * @apiSuccess (Error 40004) {int} code 40004 校验码错误
     * @apiSuccess (Error 40005) {int} code 40005 请求验证码太频繁，请稍后再试
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> requestVCode(Http.Request request) {
        return CompletableFuture.supplyAsync(() -> {
            JsonNode node = request.body().asJson();
            String phoneNumber = node.findPath("phoneNumber").asText();
            if (!ValidationUtil.isPhoneNumber(phoneNumber)) return okCustomJson(CODE40001, "手机号码有误");
            String ip = businessUtils.getRequestIP(request);
            if (!ValidationUtil.isValidIP(ip)) return okCustomJson(CODE500, "请求参数有误");
            String key = RedisKeyConstant.KEY_LOGIN_PREFIX + ip;
            Optional<Integer> optional = redis.sync().get(key);
            if (optional.isPresent()) {
                int count = optional.get();
                if (count > 10) return okCustomJson(CODE40001, "亲，服务器开小差了~");
                redis.set(key, count + 1, 2 * 60);
            } else {
                redis.set(key, 1, 2 * 60);
            }
            String existRequestKey = EXIST_REQUEST_SMS + phoneNumber;
            Optional<String> existOptional = redis.sync().get(existRequestKey);
            if (existOptional.isPresent()) {
                String exist = existOptional.get();
                if (!ValidationUtil.isEmpty(exist)) return okCustomJson(CODE40001, "一分钟内只能请求一次短信");
            }
            final String generatedVerificationCode = businessUtils.generateVerificationCode();
            String content = SMS_TEMPLATE.replace("**code**", generatedVerificationCode);
            return businessUtils.sendSingleSMS(phoneNumber, generatedVerificationCode, content)
                    .thenApplyAsync((result) -> ok(result)).toCompletableFuture().join();
        });

    }


    /**
     * @api {POST} /v1/cp/request_user_vcode/ 02请求给用户号码发短信
     * @apiName requestUserVCode
     * @apiGroup Admin-SMS
     * @apiSuccess (Success 200){int} code 200 成功发送短信
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     * @apiSuccess (Error 40002) {int} code 40002 发送失败
     * @apiSuccess (Error 40003) {int} code 40003 达到限制次数
     * @apiSuccess (Error 40004) {int} code 40004 校验码错误
     * @apiSuccess (Error 40005) {int} code 40005 请求验证码太频繁，请稍后再试
     */
    public CompletionStage<Result> requestUserVCode(Http.Request request) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            if (null == admin) return unauth403();
            String phoneNumber = admin.userName;
            if (!ValidationUtil.isPhoneNumber(phoneNumber)) {
                phoneNumber = admin.phoneNumber;
            }
            if (!ValidationUtil.isPhoneNumber(phoneNumber)) return okCustomJson(CODE40001, "你的个人资料未填写手机号码");
            final String generatedVerificationCode = businessUtils.generateVerificationCode();
            String content = SMS_TEMPLATE.replace("**code**", generatedVerificationCode);
            return businessUtils.sendSingleSMS(phoneNumber, generatedVerificationCode, content)
                    .thenApplyAsync((result) -> ok(result)).toCompletableFuture().join();
        });

    }

}
