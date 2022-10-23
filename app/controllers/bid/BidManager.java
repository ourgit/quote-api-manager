package controllers.bid;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import constants.BusinessConstant;
import controllers.BaseAdminSecurityController;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import models.bid.Bid;
import models.bid.BidDetail;
import models.bid.BidUser;
import play.Logger;
import play.i18n.Messages;
import play.i18n.MessagesApi;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


/**
 * 报价控制类
 */
public class BidManager extends BaseAdminSecurityController {

    Logger.ALogger logger = Logger.of(BidManager.class);
    @Inject
    MessagesApi messagesApi;

    /**
     * @api {POST} /v1/cp/bid/bid_list/ 01报价列表
     * @apiName listBid
     * @apiGroup Admin-Bid
     * @apiParam {long} page page
     * @apiParam {long} size size
     * @apiParam {int} status  status10未报价 20报价中 30执行中 40已完成
     * @apiSuccess (Success 200) {int} code 200
     * @apiSuccess (Success 200) {JsonArray} list 列表
     * @apiSuccess (Success 200) {String} serviceRegion 服务区域
     * @apiSuccess (Success 200) {String} serviceAddress 服务地址
     * @apiSuccess (Success 200) {String} categoryName 分类名字
     * @apiSuccess (Success 200) {String} preferenceServiceTime 预约时间
     * @apiSuccess (Success 200) {String} serviceContent 服务内容
     * @apiSuccess (Success 200) {String} contactMail 联系人邮箱
     * @apiSuccess (Success 200) {String} contactName 联系人名字
     * @apiSuccess (Success 200) {int} status  10未报价 20报价中 30执行中 40已完成
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> listBid(Http.Request request) {
        return CompletableFuture.supplyAsync(() -> {
            JsonNode jsonNode = request.body().asJson();
            Messages messages = this.messagesApi.preferred(request);
            String baseArgumentError = messages.at("base.argument.error");
            if (null == jsonNode) return okCustomJson(CODE40001, baseArgumentError);
            int page = jsonNode.findPath("page").asInt();
            int size = jsonNode.findPath("size").asInt();
            int status = jsonNode.findPath("status").asInt();
            if (page < 1) page = 1;
            if (size < 1) size = BusinessConstant.PAGE_SIZE_20;
            ExpressionList<Bid> expressionList = Bid.find.query().where();
            if (status > 0) expressionList.eq("status", status);
            PagedList<Bid> pagedList = expressionList
                    .orderBy().desc("id")
                    .setFirstRow((page - 1) * size)
                    .setMaxRows(size)
                    .findPagedList();
            List<Bid> list = pagedList.getList();
            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            result.put("pages", pagedList.getTotalPageCount());
            result.put("hasNext", pagedList.hasNext());
            result.set("list", Json.toJson(list));
            return ok(result);
        });
    }

    /**
     * @api {GET} /v1/cp/bid/bid_list/:id/ 02报价单详情
     * @apiName getBid
     * @apiGroup Admin-Bid
     * @apiSuccess (Success 200) {int} code 200
     * @apiSuccess (Success 200) {JsonArray} list 列表
     * @apiSuccess (Success 200) {String} serviceRegion 服务区域
     * @apiSuccess (Success 200) {String} serviceAddress 服务地址
     * @apiSuccess (Success 200) {String} categoryName 分类名字
     * @apiSuccess (Success 200) {String} preferenceServiceTime 预约时间
     * @apiSuccess (Success 200) {String} serviceContent 服务内容
     * @apiSuccess (Success 200) {String} contactPhoneNumber 联系电话
     * @apiSuccess (Success 200) {String} fileList 附件
     * @apiSuccess (Success 200) {String} contactMail 联系人邮箱
     * @apiSuccess (Success 200) {String} contactName 联系人名字
     */
    public CompletionStage<Result> getBid(Http.Request request, long id) {
        return CompletableFuture.supplyAsync(() -> {
            Bid bid = Bid.find.byId(id);
            if (null == bid) return okCustomJson(CODE40001, "该报价不存在");
            List<BidUser> bidUserList = BidUser.find.query().where()
                    .eq("bidId", id)
                    .findList();
            bid.bidUserList.addAll(bidUserList);
            List<BidDetail> detailList = BidDetail.find.query().where()
                    .eq("bidId", id)
                    .findList();
            bid.detailList.addAll(detailList);
            ObjectNode result = (ObjectNode) Json.toJson(bid);
            result.put(CODE, CODE200);
            return ok(result);
        });
    }


    /**
     * @api {POST} /v1/cp/bid/:id/ 03更改报价状态
     * @apiName updateBid
     * @apiGroup BID
     * @apiParam {int} status －1下架 10上架 20报价中 30施工中 40已完成
     * @apiSuccess (Success 200) {int} code 200
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> updateBid(Http.Request request, long id) {
        return CompletableFuture.supplyAsync(() -> {
            JsonNode jsonNode = request.body().asJson();
            Messages messages = this.messagesApi.preferred(request);
            String baseArgumentError = messages.at("base.argument.error");
            if (null == jsonNode) return okCustomJson(CODE40001, baseArgumentError);
            Bid bid = Bid.find.byId(id);
            if (null == bid) return okCustomJson(CODE40001, "该报价不存在");

            int status = jsonNode.findPath("status").asInt();
            long currentTime = dateUtils.getCurrentTimeBySecond();
            bid.setUpdateTime(currentTime);
            bid.setStatus(status);
            bid.save();
            return okJSON200();
        });
    }
}
