package controllers.basic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import constants.BusinessConstant;
import controllers.BaseAdminSecurityController;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import models.admin.AdminMember;
import models.shop.NewShopPlan;
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
 * 系统配置管理类
 */
public class NewShopPlanManager extends BaseAdminSecurityController {
    /**
     * @api {GET} /v1/cp/new_shop_plan_list/?page=&filter=&clientType=&bizType 01查看新店筹备列表
     * @apiName listNewShopPlan
     * @apiGroup ADMIN-NEW-PLAN
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess {JsonArray} list 列表
     * @apiSuccess {int} id id
     * @apiSuccess {string} name 名称
     * @apiSuccess {string} imgUrl 图片链接地址
     * @apiSuccess {string} linkUrl 链接地址
     * @apiSuccess {string} coverUrl 角标地址
     * @apiSuccess {int} clientType 客户端类型 1PC 2手机
     * @apiSuccess {int} bizType 业务类型
     * @apiSuccess {string} note 备注
     * @apiSuccess {string} title1 title1
     * @apiSuccess {string} title2 title2
     * @apiSuccess {int} sort 显示顺序
     * @apiSuccess {boolean} needShow  是否显示
     * @apiSuccess (Error 500) {int} code 500 未知错误
     */
    public CompletionStage<Result> listNewShopPlan(int page, String filter, int clientType, int bizType) {
        return CompletableFuture.supplyAsync(() -> {
            ExpressionList<NewShopPlan> expressionList = NewShopPlan.find.query().where();
            if (clientType > 0) expressionList.eq("clientType", clientType);
            if (bizType > 0) expressionList.eq("bizType", bizType);
            if (!ValidationUtil.isEmpty(filter)) expressionList.icontains("name", filter);
            PagedList<NewShopPlan> pagedList = expressionList
                    .setFirstRow((page - 1) * BusinessConstant.PAGE_SIZE_20)
                    .setMaxRows(BusinessConstant.PAGE_SIZE_20)
                    .findPagedList();
            int pages = pagedList.getTotalPageCount();
            List<NewShopPlan> resultList = pagedList.getList();
            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            result.set("list", Json.toJson(resultList));
            result.put("pages", pages);
            return ok(result);
        });

    }

    /**
     * @api {GET} /v1/cp/new_shop_plan/:id/ 02查看新店筹备详情
     * @apiName getShopPlan
     * @apiGroup ADMIN-NEW-PLAN
     * @apiSuccess {int} id id
     * @apiSuccess {string} name 名称
     * @apiSuccess {string} imgUrl 图片链接地址
     * @apiSuccess {string} linkUrl 链接地址
     * @apiSuccess {string} coverUrl 角标地址
     * @apiSuccess {int} clientType 客户端类型 1PC 2手机
     * @apiSuccess {int} bizType 业务类型
     * @apiSuccess {string} note 备注
     * @apiSuccess {string} title1 title1
     * @apiSuccess {string} title2 title2
     * @apiSuccess {int} sort 显示顺序
     * @apiSuccess {boolean} needShow  是否显示
     * @apiSuccess (Error 500) {int} code 500 未知错误
     */
    public CompletionStage<Result> getShopPlan(int id) {
        return CompletableFuture.supplyAsync(() -> {
            if (id < 1) return okCustomJson(CODE40001, "参数错误");
            NewShopPlan shopPlan = NewShopPlan.find.byId(id);
            ObjectNode node = (ObjectNode) Json.toJson(shopPlan);
            node.put(CODE, CODE200);
            return ok(node);
        });
    }

    /**
     * @api {POST} /v1/cp/new_shop_plan/new/ 03添加新店筹备
     * @apiName addNewShopPlan
     * @apiGroup ADMIN-NEW-PLAN
     * @apiParam {string} name 名称
     * @apiParam {string} imgUrl 图片链接地址
     * @apiParam {string} linkUrl 链接地址
     * @apiParam {string} coverUrl 角标地址
     * @apiParam {int} clientType 客户端类型 1PC 2手机
     * @apiParam {int} bizType 业务类型
     * @apiParam {string} note 备注
     * @apiParam {string} title1 title1
     * @apiParam {string} title2 title2
     * @apiParam {int} sort 显示顺序
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> addNewShopPlan(Http.Request request) {
        JsonNode requestNode = request.body().asJson();
        Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
        return CompletableFuture.supplyAsync(() -> {
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            if (null == requestNode) return okCustomJson(CODE40001, "参数错误");
            NewShopPlan newShopPlan = Json.fromJson(requestNode, NewShopPlan.class);
            if (null == newShopPlan) return okCustomJson(CODE40001, "参数错误");
            if (ValidationUtil.isEmpty(newShopPlan.getImgUrl()))
                return okCustomJson(CODE40001, "请上传图片");
            long currentTime = dateUtils.getCurrentTimeBySecond();
            newShopPlan.setUpdateTime(currentTime);
            newShopPlan.setCreateTime(currentTime);
            newShopPlan.setNeedShow(true);
            newShopPlan.save();
            businessUtils.addOperationLog(request, admin, "添加新店筹备：" + newShopPlan.toString());
            return okJSON200();
        });
    }

    /**
     * @api {POST} /v1/cp/new_shop_plan/:id/ 04修改新店筹备
     * @apiName updateShopPlan
     * @apiGroup ADMIN-NEW-PLAN
     * @apiParam {string} name 名称
     * @apiParam {string} imgUrl 图片链接地址
     * @apiParam {string} linkUrl 链接地址
     * @apiParam {string} coverUrl 角标地址
     * @apiParam {int} clientType 客户端类型 1PC 2手机
     * @apiParam {int} bizType 业务类型
     * @apiParam {string} note 备注
     * @apiParam {string} title1 title1
     * @apiParam {string} title2 title2
     * @apiParam {int} sort 显示顺序
     * @apiParam {int} needShow 是否显示
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> updateShopPlan(Http.Request request, int id) {
        JsonNode requestNode = request.body().asJson();
        Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
        return CompletableFuture.supplyAsync(() -> {
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            if (id < 1) return okCustomJson(CODE40001, "参数错误");
            if (null == requestNode) return okCustomJson(CODE40001, "参数错误");
            NewShopPlan param = Json.fromJson(requestNode, NewShopPlan.class);
            if (null == param) return okCustomJson(CODE40001, "参数错误");
            NewShopPlan newShopPlan = NewShopPlan.find.byId(id);
            if (null == newShopPlan) return okCustomJson(CODE40002, "未找到该轮播");
            if (requestNode.has("name")) newShopPlan.setName(param.getName());
            if (requestNode.has("imgUrl")) newShopPlan.setImgUrl(param.getImgUrl());
            if (requestNode.has("linkUrl")) newShopPlan.setLinkUrl(param.getLinkUrl());
            if (requestNode.has("coverUrl")) newShopPlan.setCoverUrl(param.getCoverUrl());
            if (requestNode.has("sort")) newShopPlan.setSort(param.getSort());
            if (requestNode.has("note")) newShopPlan.setNote(param.getNote());
            if (param.getClientType() > 0) newShopPlan.setClientType(param.getClientType());
            if (param.getBizType() > 0) newShopPlan.setBizType(param.getBizType());
            if (requestNode.has("needShow")) newShopPlan.setNeedShow(param.isNeedShow());
            if (!ValidationUtil.isEmpty(param.getTitle1())) newShopPlan.setTitle1(param.getTitle1());
            if (!ValidationUtil.isEmpty(param.getTitle2())) newShopPlan.setTitle2(param.getTitle2());
            newShopPlan.setUpdateTime(dateUtils.getCurrentTimeBySecond());
            newShopPlan.save();
            businessUtils.addOperationLog(request, admin, "修改新店筹备，修改前：" + param.toString() + "，修改后" + newShopPlan.toString());
            return okJSON200();
        });
    }

    /**
     * @api {POST} /v1/cp/new_shop_plan/ 05删除新店筹备
     * @apiName delShopPlan
     * @apiGroup ADMIN-NEW-PLAN
     * @apiParam {int} id id
     * @apiParam {String} operation 操作,"del"为删除
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     * @apiSuccess (Error 40001) {int} code 40002 轮播不存在
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> delShopPlan(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
        return CompletableFuture.supplyAsync(() -> {
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            String operation = jsonNode.findPath("operation").asText();
            if (ValidationUtil.isEmpty(operation) || !operation.equals("del")) return okCustomJson(CODE40001, "参数错误");
            int id = jsonNode.findPath("id").asInt();
            if (id < 1) return okCustomJson(CODE40001, "参数错误");
            NewShopPlan newShopPlan = NewShopPlan.find.byId(id);
            if (null == newShopPlan) return okCustomJson(CODE40002, "该新店筹备不存在");
            businessUtils.addOperationLog(request, admin, "删除新店筹备" + newShopPlan.toString());
            newShopPlan.delete();
            return okJSON200();
        });
    }

}
