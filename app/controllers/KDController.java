package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.Logger;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * 电子面单快递帮助类
 */
@Singleton
public class KDController extends BaseAdminSecurityController {
    Logger.ALogger logger = Logger.of(KDController.class);
    @Inject
    WSClient ws;

    /**
     * @api {GET} /v1/o/express_query/?deliveryNo=&phoneNumber= 46物流查询
     * @apiName expressQuery
     * @apiGroup Order
     * @apiDescription {"status":0,"msg":"ok","result":{"number":"SF1407021096548","type":"sfexpress","typename":"顺丰速运","logo":"https://api.jisuapi.com/express/static/images/logo/80/sfexpress.png","list":[{"time":"2021-05-19 11:33:19","status":"在官网\"运单资料&签收图\",可查看签收人信息"},{"time":"2021-05-19 11:33:18","status":"您的快件已签收，如有疑问请电联快递员【王诗洁，电话：13695058037】。疫情期间顺丰每日对网点消毒、快递员每日测温、配戴口罩，感谢您使用顺丰，期待再次为您服务。（主单总件数：1件）"},{"time":"2021-05-19 09:03:02","status":"快件交给王诗洁,正在派送途中（联系电话：13695058037,顺丰已开启“安全呼叫”保护您的电话隐私,请放心接听！）（主单总件数：1件）"},{"time":"2021-05-19 08:33:04","status":"正在派送途中,请您准备签收(派件人:王诗洁,电话:13695058037)"},{"time":"2021-05-19 07:46:23","status":"快件到达 【南平邵武元隆大厦营业点】"},{"time":"2021-05-19 05:20:59","status":"快件已发车"},{"time":"2021-05-19 05:20:42","status":"快件在【南平延平集散点】完成分拣,准备发往 【南平邵武元隆大厦营业点】"},{"time":"2021-05-19 04:09:11","status":"快件到达 【南平延平集散点】"},{"time":"2021-05-19 01:19:15","status":"快件已发车"},{"time":"2021-05-19 00:53:13","status":"快件在【福州兰圃中转场】完成分拣,准备发往 【南平延平集散点】"},{"time":"2021-05-18 22:18:29","status":"快件已发车"},{"time":"2021-05-18 22:09:00","status":"快件在【阳下速运营业点】完成分拣,准备发往 【福州兰圃中转场】"},{"time":"2021-05-18 19:35:38","status":"顺丰速运 已收取快件"},{"time":"2021-05-18 19:11:40","status":"顺丰速运 已收取快件"}],"deliverystatus":3,"issign":1},"code":200}
     * @apiSuccess (Success 200){int} code 200
     * @apiSuccess (Error 40001){int} code 40001 参数错误
     * @apiSuccess (Error 40002){int} code 40002 只能确认自己的订单
     * @apiSuccess (Error 40003){int} code 40003 该商品不存在
     */
    public CompletionStage<Result> expressQuery(String deliveryNo, String phoneNumber) {
        return CompletableFuture.supplyAsync(() -> {
            String hostUrl = businessUtils.getKDHostUrl();
            String appcode = businessUtils.getKDAppCode();
            logger.info("hostUrl:" + hostUrl);
            logger.info("appcode:" + appcode);
            //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
            String postStr = "?mobile=" + phoneNumber + "&number=" + deliveryNo + "&type=auto";
            return ws.url(hostUrl + postStr)
                    .addHeader("Authorization", "APPCODE " + appcode)
                    .addHeader("accept", "*/*")
                    .addHeader("Content-Type", "application/json; charset=UTF-8")
                    .addHeader("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)")
                    .get()
                    .thenApplyAsync((wsResponse) -> {
                        JsonNode resultNode = wsResponse.asJson();
                        if (null != resultNode) {
                            int status = resultNode.findPath("status").asInt();
                            if (status != 0) {
                                logger.info("expressQuery:" + resultNode.toString());
                                String reason = resultNode.findPath("Reason").asText();
                                return okCustomJson(CODE500, reason);
                            } else {
                                ObjectNode result = (ObjectNode) resultNode;
                                result.put(CODE, CODE200);
                                return ok(result);
                            }
                        }
                        return okCustomJson(CODE500, "请求发生异常，请重试");
                    }).toCompletableFuture().join();
        });
    }

    private ArrayNode reverseTime(ArrayNode traces) {
        ArrayNode nodes = Json.newArray();
        if (null != traces) {
            traces.forEach((each) -> nodes.insert(0, each));
        }
        return nodes;
    }
}
