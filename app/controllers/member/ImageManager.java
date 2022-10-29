package controllers.member;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import constants.BusinessConstant;
import controllers.BaseAdminSecurityController;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import models.admin.AdminMember;
import models.user.Image;
import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import utils.ValidationUtil;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


public class ImageManager extends BaseAdminSecurityController {
    /**
     * @api {GET} /v1/cp/images/?page= 01图片列表
     * @apiName listImages
     * @apiGroup ADMIN-IMAGE
     * @apiSuccess (Success 200){int} code 200
     * @apiSuccess (Success 200){int} pages 页码
     * @apiSuccess (Success 200){JsonArray} list 订单列表
     * @apiSuccess (Success 200){String} image image
     * @apiSuccess (Success 200){String} createTime 上传时间
     */
    public CompletionStage<Result> listImages(int page) {
        return CompletableFuture.supplyAsync(() -> {
            ExpressionList<Image> expressionList = Image.find.query().where();
            PagedList<Image> pagedList = expressionList.orderBy().desc("id")
                    .setFirstRow((page - 1) * BusinessConstant.PAGE_SIZE_20)
                    .setMaxRows(BusinessConstant.PAGE_SIZE_20)
                    .findPagedList();
            int pages = pagedList.getTotalPageCount();
            List<Image> list = pagedList.getList();
            ObjectNode result = Json.newObject();
            result.put("pages", pages);
            result.put(CODE, CODE200);
            result.set("list", Json.toJson(list));
            return ok(result);
        });
    }

    /**
     * @api {POST} /v1/cp/images/new/ 02添加图片
     * @apiName addImages
     * @apiGroup ADMIN-IMAGE
     * @apiParam {long} productId 商品id
     * @apiParam {JsonArray} imgList 图片列表
     * @apiParam {string} imgUrl 图片链接地址
     * @apiParam {int} imgDisplayOrder 图片排序
     * @apiParam {string} imgTips 图片提示信息
     * @apiSuccess (Success 200){int} code 200
     * @apiSuccess (Error 40001){int} code 40001 参数错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> addImages(Http.Request request) {
        JsonNode requestNode = request.body().asJson();
        Optional<AdminMember> optional = businessUtils.getAdminByAuthToken(request);
        return CompletableFuture.supplyAsync(() -> {
            if (!optional.isPresent()) return unauth403();
            AdminMember member = optional.get();
            if (null == member) return unauth403();
            if (null == requestNode) return okCustomJson(CODE40001, "参数错误");
            String image = requestNode.findPath("image").asText();
            Image mhImage = new Image();
            mhImage.setImage(image);
            mhImage.setCreateTime(dateUtils.getCurrentTimeBySecond());
            mhImage.save();
            return okJSON200();
        });
    }


    /**
     * @api {POST} /v1/cp/images/:imageId/ 02修改图片
     * @apiName updateImage
     * @apiGroup ADMIN-IMAGE
     * @apiParam {string} image 图片链接地址
     * @apiSuccess (Success 200){int} code 200
     * @apiSuccess (Error 40001){int} code 40001 参数错误
     * @apiSuccess (Error 40002){int} code 40002 商品属性不存在
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> updateImage(Http.Request request, long imageId) {
        JsonNode requestNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            if (null == requestNode || imageId < 1) return okCustomJson(CODE40001, "参数错误");
            Image image = Image.find.byId(imageId);
            if (null == image) return okCustomJson(CODE40002, "图片不存在");
            String imgUrl = requestNode.findPath("image").asText();
            image.setImage(imgUrl);
            image.save();
            return okJSON200();
        });
    }

    /**
     * @api {POST} /v1/cp/images/ 03删除图片
     * @apiName deleteImages
     * @apiGroup ADMIN-IMAGE
     * @apiParam {int} imageId 图片id
     * @apiParam {String} operation 操作,"del"为删除
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> deleteImages(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            String operation = jsonNode.findPath("operation").asText();
            if (ValidationUtil.isEmpty(operation) || !operation.equals("del")) return okCustomJson(CODE40001, "参数错误");
            long imageId = jsonNode.findPath("imageId").asLong();
            Image image = Image.find.byId(imageId);
            if (null == image) return okCustomJson(CODE40002, "图片不存在");
            image.delete();
            return okJSON200();
        });

    }

}
