package controllers.product;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import constants.BusinessConstant;
import controllers.BaseAdminSecurityController;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import models.shop.Shop;
import models.shop.Showcase;
import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import utils.ValidationUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ShowCaseManager extends BaseAdminSecurityController {

    /**
     * @api {GET} /v1/cp/show_case_list/?shopId=&page=&title= 01案例图片
     * @apiName listShowCase
     * @apiGroup ADMIN-SHOW-CASE
     * @apiSuccess (Success 200){int} code 200
     * @apiSuccess (Success 200){Array} list
     * @apiSuccess (Success 200){String} title 标题
     * @apiSuccess (Success 200){String} tags 标签
     * @apiSuccess (Success 200){String} images 图片
     * @apiSuccess (Success 200){String} shopName 店铺名字
     * @apiSuccess (Success 200){long} shopId 店铺ID
     * @apiSuccess (Success 200){long} imageCount 图片数
     * @apiSuccess (Error 40001){int} code 40001 参数错误
     */
    public CompletionStage<Result> listShowCase(Http.Request request, long shopId, int page, String title) {
        return CompletableFuture.supplyAsync(() -> {
            ExpressionList<Showcase> expressionList = Showcase.find.query().where();
            if (shopId > 0) expressionList.eq("shopId", shopId);
            if (!ValidationUtil.isEmpty(title)) expressionList.icontains("title", title);
            PagedList<Showcase> pagedList = expressionList.orderBy().desc("id")
                    .setFirstRow((page - 1) * BusinessConstant.PAGE_SIZE_10)
                    .setMaxRows(BusinessConstant.PAGE_SIZE_10)
                    .findPagedList();
            ObjectNode result = Json.newObject();
            result.set("list", Json.toJson(pagedList.getList()));
            result.put("pages", pagedList.getTotalPageCount());
            result.put(CODE, CODE200);
            return ok(result);
        });
    }

    /**
     * @api {GET} /v1/cp/show_case_list/:id/ 02案例图片详情
     * @apiName getShowCase
     * @apiGroup ADMIN-SHOW-CASE
     * @apiSuccess (Success 200){int} code 200
     * @apiSuccess (Success 200){String} title 标题
     * @apiSuccess (Success 200){String} tags 标签
     * @apiSuccess (Success 200){String} images 图片
     * @apiSuccess (Success 200){String} shopName 店铺名字
     * @apiSuccess (Success 200){long} shopId 店铺ID
     * @apiSuccess (Success 200){long} imageCount 图片数
     * @apiSuccess (Error 40001){int} code 40001 参数错误
     */
    public CompletionStage<Result> getShowCase(Http.Request request, long id) {
        return CompletableFuture.supplyAsync(() -> {
            Showcase showcase = Showcase.find.byId(id);
            ObjectNode result = (ObjectNode) Json.toJson(showcase);
            result.put(CODE, CODE200);
            return ok(result);
        });
    }

    /**
     * @api {POST} /v1/cp/show_case/new/ 03添加案例图片
     * @apiName addShowCase
     * @apiGroup ADMIN-SHOW-CASE
     * @apiParam {String} title 标题
     * @apiParam {String} tags 标签
     * @apiParam {String} images 图片
     * @apiParam {long} shopId 店铺ID
     * @apiParam {long} imageCount 图片数
     * @apiSuccess (Success 200){int} code 200
     * @apiSuccess (Error 40001){int} code 40001 参数错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> addShowCase(Http.Request request) {
        JsonNode requestNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            if (null == requestNode) return okCustomJson(CODE40001, "参数错误");
            Showcase showcase = Json.fromJson(requestNode, Showcase.class);
            if (ValidationUtil.isEmpty(showcase.title)) return okCustomJson(CODE40001, "请输入标题");
            if (ValidationUtil.isEmpty(showcase.images)) return okCustomJson(CODE40001, "请上传图片");
            Shop shop = Shop.find.byId(showcase.shopId);
            if (null == shop) return okCustomJson(CODE40001, "shopId有误");
            showcase.setId(0);
            showcase.setShopName(shop.name);
            showcase.setStatus(Showcase.STATUS_NOT_AUDIT);
            showcase.setCreateTime(dateUtils.getCurrentTimeBySecond());
            showcase.save();
            return okJSON200();
        });
    }


    /**
     * @api {POST} /v1/cp/show_case/:imageId/ 04修改案例图片
     * @apiName updateShowCase
     * @apiGroup ADMIN-SHOW-CASE
     * @apiParam {String} title 标题
     * @apiParam {String} tags 标签
     * @apiParam {String} images 图片
     * @apiParam {long} imageCount 图片数
     * @apiSuccess (Success 200){int} code 200
     * @apiSuccess (Error 40001){int} code 40001 参数错误
     * @apiSuccess (Error 40002){int} code 40002 案例图片不存在
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> updateShowCase(Http.Request request, long id) {
        JsonNode requestNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            if (null == requestNode || id < 1) return okCustomJson(CODE40001, "参数错误");
            Showcase showcase = Showcase.find.byId(id);
            if (null == showcase) return okCustomJson(CODE40002, "案例图片不存在");

            String title = requestNode.findPath("title").asText();
            String tags = requestNode.findPath("tags").asText();
            String images = requestNode.findPath("images").asText();
            long imageCount = requestNode.findPath("imageCount").asLong();
            if (requestNode.has("title")) showcase.setTitle(title);
            if (requestNode.has("tags")) showcase.setTags(tags);
            if (requestNode.has("images")) showcase.setImages(images);
            if (requestNode.has("imageCount")) showcase.setImageCount(imageCount);
            showcase.save();
            return okJSON200();
        });
    }

    /**
     * @api {POST} /v1/cp/show_case/ 05删除案例图片
     * @apiName deleteShowcase
     * @apiGroup ADMIN-SHOW-CASE
     * @apiParam {int} id 案例id
     * @apiParam {String} operation 操作,"del"为删除
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     * @apiSuccess (Error 40002) {int} code 40002 商品属性不存在
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> deleteShowcase(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            String operation = jsonNode.findPath("operation").asText();
            if (ValidationUtil.isEmpty(operation) || !operation.equals("del")) return okCustomJson(CODE40001, "参数错误");
            long id = jsonNode.findPath("id").asInt();
            if (id < 1) return okCustomJson(CODE40001, "参数错误");
            Showcase showcase = Showcase.find.byId(id);
            if (null == showcase) return okCustomJson(CODE40002, "案例图片不存在");
            showcase.delete();
            return okJSON200();
        });

    }

    /**
     * @api {POST} /v1/cp/show_case_audit/:id/ 06审核案例图片
     * @apiName auditShowCase
     * @apiGroup ADMIN-SHOW-CASE
     * @apiParam {int} status 1未审核 2审核通过 -1驳回
     * @apiParam {int} sort sort
     * @apiParam {boolean} placeTop true/false
     * @apiSuccess (Success 200){int} code 200
     * @apiSuccess (Error 40001){int} code 40001 参数错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> auditShowCase(Http.Request request, long id) {
        JsonNode requestNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            if (null == requestNode || id < 1) return okCustomJson(CODE40001, "参数错误");
            Showcase showcase = Showcase.find.byId(id);
            if (null == showcase) return okCustomJson(CODE40002, "案例图片不存在");
            int status = requestNode.findPath("status").asInt();
            int sort = requestNode.findPath("sort").asInt();
            if (requestNode.has("status")) showcase.setStatus(status);
            if (requestNode.has("sort")) showcase.setSort(sort);
            boolean placeTop = requestNode.findPath("placeTop").asBoolean();
            if (requestNode.has("placeTop")) showcase.setPlaceTop(placeTop);
            showcase.save();
            return okJSON200();
        });
    }
}
