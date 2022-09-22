package controllers.basic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.BaseAdminSecurityController;
import play.Logger;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.*;

import static utils.wechatpay.WechatConfig.MENU_CREATE_URL;
import static utils.wechatpay.WechatConfig.MENU_GET_URL;


/**
 * 微信管理
 */
public class WechatManager extends BaseAdminSecurityController {
    Logger.ALogger logger = Logger.of(WechatManager.class);

    @Inject
    WSClient wsClient;

    /**
     * @api {GET} /v1/cp/wechat_menu/ 01微信菜单查询
     * @apiName getMenu
     * @apiGroup Admin-Wechat
     * @apiSuccess (Success 200){int} code 200
     */
    public CompletionStage<Result> getWechatMenu() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String resultUrl = MENU_GET_URL.replace("ACCESS_TOKEN", businessUtils.getAccessToken());
                logger.info("resultUrl:" + resultUrl);
                WSResponse response = wsClient.url(resultUrl).get().toCompletableFuture().get(10, TimeUnit.SECONDS);
                if (null != response) {
                    JsonNode resultNode = response.asJson();
                    if (null != resultNode) {
                        ObjectNode node = (ObjectNode) resultNode;
                        node.put(CODE, CODE200);
                        return ok(node);
                    }
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            return okCustomJson(CODE500, "发生异常");
        });
    }

    /**
     * @api {POST} /v1/cp/wechat_menu/ 02创建微信菜单
     * @apiName createMenu
     * @apiGroup Admin-Wechat
     * @apiSuccess (Success 200){int} code 200
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> createMenu(Http.Request request) {
        JsonNode menu = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            if (null == menu) return okCustomJson(CODE40001, "参数错误");
            try {
                String resultUrl = MENU_CREATE_URL.replace("ACCESS_TOKEN", businessUtils.getAccessToken());
                logger.info("resultUrl:" + resultUrl);
                WSResponse response = wsClient.url(resultUrl).post(menu).toCompletableFuture().get(10, TimeUnit.SECONDS);
                if (null != response) {
                    JsonNode resultNode = response.asJson();
                    int errcode = resultNode.findPath("errcode").asInt();
                    String errmsg = resultNode.findPath("errmsg").asText();
                    if (errcode == 0 && errmsg.equalsIgnoreCase("ok")) return okJSON200();
                    else return okCustomJson(CODE500, errmsg);
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            return okCustomJson(CODE500, "发生异常");
        });
    }
}
