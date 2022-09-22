package controllers.basic;

import constants.RedisKeyConstant;
import controllers.BaseAdminSecurityController;
import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Result;
import services.AppInit;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static constants.RedisKeyConstant.ADMIN_GROUP_ACTION_KEY_SET;

/**
 * Created by win7 on 2016/8/8.
 */
public class CacheManager extends BaseAdminSecurityController {
    private final AppInit appInit;

    @Inject
    public CacheManager(AppInit appInit) {
        this.appInit = appInit;
    }

    /**
     * @api {GET} /v1/cp/cache/refresh_admin_action/  01刷新管理员权限缓存
     * @apiName refreshAdminAction
     * @apiGroup ADMIN-CACHE
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 500) {int} code 500 未知错误
     */
    public CompletionStage<Result> refreshAdminAction() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                syncCache.remove(ADMIN_GROUP_ACTION_KEY_SET);
                appInit.saveAdminCache();
                return okJSON200();
            } catch (Exception e) {
                Logger.error(e.getMessage());
                return okCustomJson(CODE500, "refreshAdminAction:" + e.getMessage());
            }
        });
    }


    /**
     * @api {POST} /v1/cp/cache/set_maintenance/  02进入维护模式
     * @apiName setMaintenance
     * @apiGroup ADMIN-CACHE
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 500) {int} code 500 未知错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> setMaintenance() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                syncCache.set(RedisKeyConstant.KEY_SYSTEM_UNDER_MAINTAINCE, RedisKeyConstant.KEY_SYSTEM_UNDER_MAINTAINCE);
                return okJSON200();
            } catch (Exception e) {
                Logger.error(e.getMessage());
                return okCustomJson(CODE500, e.getMessage());
            }
        });
    }

    /**
     * @api {POST} /v1/cp/cache/cancel_maintenance/  03退出维护模式
     * @apiName cancelMaintenance
     * @apiGroup ADMIN-CACHE
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 500) {int} code 500 未知错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> cancelMaintenance() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                syncCache.remove(RedisKeyConstant.KEY_SYSTEM_UNDER_MAINTAINCE);
                return okJSON200();
            } catch (Exception e) {
                Logger.error(e.getMessage());
                return okCustomJson(CODE500, e.getMessage());
            }
        });
    }

}
