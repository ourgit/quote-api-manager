package controllers.ad;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import constants.BusinessConstant;
import controllers.BaseAdminSecurityController;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import models.ad.Ad;
import models.ad.AdOwner;
import models.admin.AdminMember;
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

/**
 * 广告管理
 */
public class AdManager extends BaseAdminSecurityController {

    /**
     * @api {POST} /v1/cp/ad_list/  01广告列表
     * @apiName listAd
     * @apiGroup ADMIN-AD
     * @apiParam {int} [status] 1正常  -1下架
     * @apiParam {String} position position
     * @apiParam {String} dimension dimension
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200) {JsonArray} list 菜单列表
     * @apiSuccess (Success 200) {long} id id
     * @apiSuccess (Success 200)　{String} position 位置
     * @apiSuccess (Success 200)　{String} dimension　尺寸
     * @apiSuccess (Success 200)　{long} price　价格
     * @apiSuccess (Success 200)　{int} days　计价天数
     * @apiSuccess (Success 200)　{String} display 展示商家数
     * @apiSuccess (Success 200)　{long} updateTime 更新时间
     * @apiSuccess (Success 200)　{long} createTime 创建时间
     * @apiSuccess (Success 200)　{Array} adOwnerList 广告主列表
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> listAd(Http.Request request) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            if (null == admin) return unauth403();
            JsonNode requestNode = request.body().asJson();
            String position = requestNode.findPath("position").asText();
            String dimension = requestNode.findPath("dimension").asText();
            int page = requestNode.findPath("page").asInt();
            int status = requestNode.findPath("status").asInt();
            ExpressionList<Ad> expressionList = Ad.find.query().where();
            if (status != 0) expressionList.eq("status", status);
            if (!ValidationUtil.isEmpty(position)) expressionList.icontains("position", position);
            if (!ValidationUtil.isEmpty(dimension)) expressionList.icontains("dimension", dimension);
            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            PagedList<Ad> pagedList = expressionList
                    .orderBy().desc("sort")
                    .setFirstRow((page - 1) * BusinessConstant.PAGE_SIZE_20)
                    .setMaxRows(BusinessConstant.PAGE_SIZE_20)
                    .findPagedList();
            int pages = pagedList.getTotalPageCount();
            List<Ad> list = pagedList.getList();
            list.parallelStream().forEach((ad) -> {
                List<AdOwner> adOwners = AdOwner.find.query().where()
                        .eq("adId", ad.id)
                        .orderBy().desc("sort")
                        .findList();
                ad.adOwnerList.addAll(adOwners);
            });
            result.set("list", Json.toJson(list));
            result.put("pages", pages);
            return ok(result);
        });
    }

    /**
     * @api {POST} /v1/cp/ad/new/ 02添加广告
     * @apiName addAd
     * @apiGroup ADMIN-AD
     * @apiParam {String} position 位置
     * @apiParam {String} dimension　尺寸
     * @apiParam {long} price　价格
     * @apiParam {int} days　计价天数
     * @apiParam {String} display 展示商家数
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> addAd(Http.Request request) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            if (null == admin) return unauth403();

            JsonNode requestNode = request.body().asJson();
            Ad param = Json.fromJson(requestNode, Ad.class);
            if (null == param) return okCustomJson(CODE40001, "参数错误");
            if (ValidationUtil.isEmpty(param.position)) return okCustomJson(CODE40001, "位置不能为空");
            if (ValidationUtil.isEmpty(param.dimension)) return okCustomJson(CODE40001, "尺寸不能为空");
            if (ValidationUtil.isEmpty(param.display)) return okCustomJson(CODE40001, "展示商家数不能为空");
            if (param.price < 1) return okCustomJson(CODE40001, "价格有误");
            if (param.days < 1) return okCustomJson(CODE40001, "天数有误");
            long currentTime = dateUtils.getCurrentTimeBySecond();
            param.setStatus(Ad.STATUS_NORMAL);
            param.setUpdateTime(currentTime);
            param.setCreateTime(currentTime);
            param.save();
            businessUtils.addOperationLog(request, admin, "添加广告：" + requestNode.toString());
            return okJSON200();
        });
    }


    /**
     * @api {POST} /v1/cp/ad/:id/ 03修改广告
     * @apiName updateAD
     * @apiGroup ADMIN-AD
     * @apiParam {String} position 位置
     * @apiParam {String} dimension　尺寸
     * @apiParam {long} price　价格
     * @apiParam {int} days　计价天数
     * @apiParam {String} display 展示商家数
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     * @apiSuccess (Error 40002) {int} code 40002 该广告不存在
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> updateAD(Http.Request request, long id) {
        JsonNode requestNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            if (id < 1) return okCustomJson(CODE40001, "参数错误");
            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            if (null == admin) return unauth403();

            Ad param = Json.fromJson(requestNode, Ad.class);
            Ad ad = Ad.find.byId(id);
            if (null == ad) return okCustomJson(CODE40002, "该广告不存在");
            businessUtils.addOperationLog(request, admin, "修改广告，改之前的值：" + ad.toString() + "修改的参数值：" + requestNode.toString());
            if (requestNode.has("position")) ad.setPosition(param.position);
            if (requestNode.has("dimension")) ad.setDimension(param.dimension);
            if (requestNode.has("price")) ad.setPrice(param.price);
            if (requestNode.has("days")) ad.setDays(param.days);
            if (requestNode.has("status")) ad.setStatus(param.status);
            if (requestNode.has("display")) ad.setDisplay(param.display);
            long currentTime = dateUtils.getCurrentTimeBySecond();
            ad.setUpdateTime(currentTime);
            ad.save();
            return okJSON200();
        });
    }


    /**
     * @api {POST} /v1/cp/ad/ 04删除广告
     * @apiName delAd
     * @apiGroup ADMIN-AD
     * @apiParam {String} id id
     * @apiParam {String} operation 操作,"del"为删除
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     * @apiSuccess (Error 40001) {int} code 40002 广告不存在
     */
    @Transactional
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> delAd(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            if (null == admin) return unauth403();
            String operation = jsonNode.findPath("operation").asText();
            if (ValidationUtil.isEmpty(operation) || !operation.equals("del")) return okCustomJson(CODE40001, "参数错误");
            long id = jsonNode.findPath("id").asLong();
            Ad ad = Ad.find.byId(id);
            if (null == ad) return okCustomJson(CODE40002, "广告不存在");
            businessUtils.addOperationLog(request, admin, "删除广告：" + ad.toString());
            ad.delete();
            return okJSON200();
        });
    }

    /**
     * @api {POST} /v1/cp/ad_owner/new/ 05添加广告主
     * @apiName addAdOwner
     * @apiGroup ADMIN-AD
     * @apiParam {long} adId　广告id
     * @apiParam {long} beginTime 开始展示时间
     * @apiParam {long} endTime 展示到期时间
     * @apiParam {int} sort sort
     * @apiParam {String} sourceUrl 素材源
     * @apiParam {String} linkUrl 跳转链接
     * @apiParam {long} uid uid
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> addAdOwner(Http.Request request) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            if (null == admin) return unauth403();

            JsonNode requestNode = request.body().asJson();
            AdOwner param = Json.fromJson(requestNode, AdOwner.class);
            if (null == param) return okCustomJson(CODE40001, "参数错误");
            if (ValidationUtil.isEmpty(param.sourceUrl)) return okCustomJson(CODE40001, "素材源不能为空");
            if (param.adId < 1) return okCustomJson(CODE40001, "请选择广告位置");
            if (param.beginTime < 1) return okCustomJson(CODE40001, "请选择生效时间");
            if (param.endTime < 1) return okCustomJson(CODE40001, "请选择到期时间");
            Ad ad = Ad.find.byId(param.adId);
            if (null == ad) return okCustomJson(CODE40001, "该广告位置不存在");
            param.setPosition(ad.position);
            param.setDimension(ad.dimension);
            long currentTime = dateUtils.getCurrentTimeBySecond();
            param.setStatus(Ad.STATUS_NORMAL);
            param.setUpdateTime(currentTime);
            param.setCreateTime(currentTime);
            param.save();
            businessUtils.addOperationLog(request, admin, "添加广告主：" + requestNode.toString());
            return okJSON200();
        });
    }

    /**
     * @api {POST} /v1/cp/ad_owner/:id/ 06修改广告主
     * @apiName updateAdOwner
     * @apiGroup ADMIN-AD
     * @apiParam {long} beginTime 开始展示时间
     * @apiParam {long} endTime 展示到期时间
     * @apiParam {int} sort sort
     * @apiParam {String} sourceUrl 素材源
     * @apiParam {String} linkUrl 跳转链接
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> updateAdOwner(Http.Request request, long id) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            if (null == admin) return unauth403();

            JsonNode requestNode = request.body().asJson();
            AdOwner adOwner = AdOwner.find.byId(id);
            if (null == adOwner) return okCustomJson(CODE40001, "该广告主不存在");

            AdOwner param = Json.fromJson(requestNode, AdOwner.class);
            if (null == param) return okCustomJson(CODE40001, "参数错误");

            long currentTime = dateUtils.getCurrentTimeBySecond();
            if (requestNode.has("status")) adOwner.setStatus(param.status);
            if (requestNode.has("beginTime")) adOwner.setBeginTime(param.beginTime);
            if (requestNode.has("endTime")) adOwner.setEndTime(param.endTime);
            if (requestNode.has("sort")) adOwner.setSort(param.sort);
            if (requestNode.has("sourceUrl")) adOwner.setSourceUrl(param.sourceUrl);
            if (requestNode.has("linkUrl")) adOwner.setLinkUrl(param.linkUrl);
            adOwner.setUpdateTime(currentTime);
            businessUtils.addOperationLog(request, admin, "修改广告主，改之前的值：" + adOwner.toString() + "修改的参数值：" + requestNode.toString());
            adOwner.save();
            return okJSON200();
        });
    }


}
