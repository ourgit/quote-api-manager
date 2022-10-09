package controllers.member;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.BaseAdminSecurityController;
import io.ebean.ExpressionList;
import models.user.Membership;
import play.Logger;
import play.cache.NamedCache;
import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import utils.ValidationUtil;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


public class MembershipManager extends BaseAdminSecurityController {

    Logger.ALogger logger = Logger.of(MembershipManager.class);
    @Inject
    @NamedCache("redis")
    protected play.cache.redis.AsyncCacheApi redis;

    /**
     * @api {GET} /v1/cp/membership_list/ 01会员价格列表
     * @apiName listMembership
     * @apiGroup ADMIN-MEMBERSHIP
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200) {int} pages 页数
     * @apiSuccess (Success 200) {JsonArray} list 列表
     * @apiSuccess (Success 200){long} id 分类id
     * @apiSuccess (Success 200){long} duration 时长，以天为单位
     * @apiSuccess (Success 200){long} oldPrice 原价
     * @apiSuccess (Success 200){long} price 现价
     * @apiSuccess (Success 200){int} sort 排序顺序
     * @apiSuccess (Success 200){string} updateTime 更新时间
     */
    public CompletionStage<Result> listMembership() {
        return CompletableFuture.supplyAsync(() -> {
            ExpressionList<Membership> expressionList = Membership.find.query().where();
            List<Membership> list = expressionList.orderBy().desc("sort").findList();
            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            result.set("list", Json.toJson(list));
            return ok(result);
        });
    }

    /**
     * @api {GET} /v1/cp/membership_list/:id/ 02会员价格详情
     * @apiName getMembership
     * @apiGroup ADMIN-MEMBERSHIP
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200){long} id 分类id
     * @apiSuccess (Success 200){long} duration 时长，以天为单位
     * @apiSuccess (Success 200){long} oldPrice 原价
     * @apiSuccess (Success 200){long} price 现价
     * @apiSuccess (Success 200){int} sort 排序顺序
     * @apiSuccess (Success 200){string} updateTime 更新时间
     */
    public CompletionStage<Result> getMembership(long id) {
        return CompletableFuture.supplyAsync(() -> {
            if (id < 1) return okCustomJson(CODE40001, "参数错误");
            Membership membership = Membership.find.byId(id);
            if (null == membership) return okCustomJson(CODE40002, "该会员价格不存在");
            ObjectNode result = (ObjectNode) Json.toJson(membership);
            result.put(CODE, CODE200);
            return ok(result);
        });
    }

    /**
     * @api {POST} /v1/cp/membership/new/ 03添加会员价格
     * @apiName addMembership
     * @apiGroup ADMIN-MEMBERSHIP
     * @apiParam {long} duration 时长，以天为单位
     * @apiParam {long} oldPrice 原价
     * @apiParam {long} price 现价
     * @apiParam {int} sort 排序顺序
     * @apiSuccess (Success 200) {int} code 200 请求成功
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> addMembership(Http.Request request) {
        JsonNode requestNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            long duration = requestNode.findPath("duration").asLong();
            long oldPrice = requestNode.findPath("oldPrice").asLong();
            long price = requestNode.findPath("price").asLong();
            long sort = requestNode.findPath("sort").asLong();
            if (duration < 1) return okCustomJson(CODE40001, "请输入时长");
            if (price < 1) return okCustomJson(CODE40001, "请输入现价");
            Membership exist = Membership.find.query().where()
                    .eq("duration", duration)
                    .setMaxRows(1)
                    .findOne();
            if (null != exist) return okCustomJson(CODE40001, "该时长的会员价格已存在");
            Membership membership = new Membership();
            membership.setDuration(duration);
            membership.setOldPrice(oldPrice);
            membership.setPrice(price);
            membership.setSort(sort);
            membership.save();
            return okJSON200();
        });
    }

    /**
     * @api {POST} /v1/cp/membership/:id/ 04修改会员价格
     * @apiName updateMembership
     * @apiGroup ADMIN-MEMBERSHIP
     * @apiParam {long} oldPrice 原价
     * @apiParam {long} price 现价
     * @apiParam {int} sort 排序顺序
     * @apiSuccess (Success 200) {int} code 200 请求成功
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> updateMembership(Http.Request request, long id) {
        JsonNode requestNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            if (id < 1) return okCustomJson(CODE40001, "参数错误");
            Membership membership = Membership.find.byId(id);
            if (null == membership) return okCustomJson(CODE40002, "该会员价格不存在");
            long oldPrice = requestNode.findPath("oldPrice").asLong();
            long price = requestNode.findPath("price").asLong();
            long sort = requestNode.findPath("sort").asLong();
            if (requestNode.has("oldPrice")) membership.setOldPrice(oldPrice);
            if (price > 0) membership.setPrice(price);
            if (sort > 0) membership.setSort(sort);
            membership.save();
            return okJSON200();
        });
    }

    /**
     * @api {POST} /v1/cp/membership/ 05删除会员价格
     * @apiName deleteMembership
     * @apiGroup ADMIN-MEMBERSHIP
     * @apiParam {int} categoryId 商品分类id
     * @apiParam {String} operation 操作,"del"为删除
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> deleteMembership(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        String operation = jsonNode.findPath("operation").asText();
        return CompletableFuture.supplyAsync(() -> {
            if (ValidationUtil.isEmpty(operation) || !operation.equals("del"))
                return okCustomJson(CODE40001, "参数错误");
            long id = jsonNode.findPath("id").asLong();
            if (id < 1) return okCustomJson(CODE40001, "参数错误");
            Membership membership = Membership.find.byId(id);
            membership.delete();
            return okJSON200();
        });
    }


}
