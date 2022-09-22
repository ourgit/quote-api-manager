package controllers.shop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import constants.BusinessConstant;
import controllers.BaseAdminController;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import models.admin.AdminMember;
import models.admin.ShopAdmin;
import models.shop.Shop;
import models.shop.ShopProfile;
import play.Logger;
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

import static constants.BusinessConstant.OPERATION_AUTH_SHOP;

/**
 *
 */
public class ShopProfileManager extends BaseAdminController {

    Logger.ALogger logger = Logger.of(ShopProfileManager.class);


    /**
     * @api {POST} /v1/cp/shop_profile_list/  01入驻待审批列表
     * @apiName listShopProfile
     * @apiGroup ADMIN-SHOP-PROFILE
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200) {JsonArray} list list
     * @apiSuccess (Success 200) {long} id id
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> listShopProfile(Http.Request request) {
        return CompletableFuture.supplyAsync(() -> {
            JsonNode requestNode = request.body().asJson();
            int status = requestNode.findPath("status").asInt();
            int page = requestNode.findPath("page").asInt();
            String filter = requestNode.findPath("filter").asText();
            ExpressionList<ShopProfile> expressionList = ShopProfile.find.query().where();
            if (status != 0) expressionList.eq("status", status);
            if (!ValidationUtil.isEmpty(filter)) expressionList.icontains("filter", filter);
            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            List<ShopProfile> list;
            if (page > 0) {
                PagedList<ShopProfile> pagedList = expressionList.orderBy().asc("id")
                        .setFirstRow((page - 1) * BusinessConstant.PAGE_SIZE_10)
                        .setMaxRows(BusinessConstant.PAGE_SIZE_10)
                        .findPagedList();
                int pages = pagedList.getTotalPageCount();
                result.put("pages", pages);
                list = pagedList.getList();
            } else {
                list = expressionList.orderBy().asc("id").findList();
            }
            result.set("list", Json.toJson(list));
            return ok(result);
        });
    }

    /**
     * @api {GET} /v1/cp/shop_profile/:id/ 02入驻店铺详情
     * @apiName getShopProfile
     * @apiGroup ADMIN-SHOP-PROFILE
     * @apiSuccess (Success 200) {long} id id
     * @apiSuccess (Success 200){String} name 名称
     * @apiSuccess (Success 200){String} contactNumber 联系电话
     * @apiSuccess (Success 200){String} contactName 联系人
     * @apiSuccess (Success 200){String} contactAddress 联系地址
     * @apiSuccess (Success 200){String} licenseNumber 营业执照号
     * @apiSuccess (Success 200){String} licenseImg 营业执照图片
     * @apiSuccess (Success 200){String} description 备注
     * @apiSuccess (Success 200){long} updateTime 更新时间
     * @apiSuccess (Success 200){long} creatorId 创建者uid
     * @apiSuccess (Success 200){long} creatorName 创建者name
     * @apiSuccess (Success 200){double} lat 纬度
     * @apiSuccess (Success 200){double} lon 经度
     * @apiSuccess (Success 200) {int} code 200 请求成功
     */
    public CompletionStage<Result> getShopProfile(Http.Request request, long id) {
        return CompletableFuture.supplyAsync(() -> {
            if (id < 1) return okCustomJson(CODE40001, "参数错误");
            ShopProfile shop = ShopProfile.find.byId(id);
            if (null == shop) return okCustomJson(CODE40002, "该店铺不存在");
            Optional<AdminMember> optional = businessUtils.getAdminByAuthToken(request);
            if (!optional.isPresent()) return unauth403();
            AdminMember admin = optional.get();
            if (null == admin) return unauth403();
            ObjectNode result = (ObjectNode) Json.toJson(shop);
            result.put(CODE, CODE200);
            return ok(result);
        });
    }

    /**
     * @api {POST} /v1/cp/shop_auth_status/ 03审核店铺入驻
     * @apiName handleShopAuth
     * @apiGroup ADMIN_MEMBER
     * @apiParam {long} id 入驻id
     * @apiParam {int} status status
     * @apiParam {String} authNote 审核备注
     * @apiSuccess (Success 200){int} code 200
     * @apiSuccess (Error 40001){int} code 40001 用户不存在
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> handleShopAuth(Http.Request request) {
        Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
        return CompletableFuture.supplyAsync(() -> {
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            if (null == admin) return unauth403();
            JsonNode requestNode = request.body().asJson();
            long id = requestNode.findPath("id").asLong();
            int status = requestNode.findPath("status").asInt();
            String authNote = requestNode.findPath("authNote").asText();
            if (id < 1) return okCustomJson(CODE40001, "参数错误");
            ShopProfile shopProfile = ShopProfile.find.byId(id);
            if (null == shopProfile) return okCustomJson(CODE40001, "该店铺不存在");
            if (!businessUtils.setLock(String.valueOf(id), OPERATION_AUTH_SHOP))
                return okCustomJson(CODE40004, "正在审核店铺入驻，请稍等");
            shopProfile.setStatus(status);
            if (!ValidationUtil.isEmpty(authNote)) shopProfile.setApproveNote(authNote);
            shopProfile.setUpdateTime(dateUtils.getCurrentTimeBySecond());
            shopProfile.save();
            if (shopProfile.status == ShopProfile.STATUS_PASS) {
                Shop shop = new Shop();
                shop.setName(shopProfile.shopName);
                shop.setRectLogo(shopProfile.rectLogo);
                shop.setContactName(shopProfile.contactName);
                shop.setContactNumber(shopProfile.contactNumber);
                shop.setContactAddress(shopProfile.contactAddress);
                shop.setStatus(Shop.STATUS_NORMAL);
                shop.setCreatorId(shopProfile.uid);
                shop.setApplyCategoriesName(shopProfile.applyCategoriesName);
                shop.setApplyCategories(convertToArray(shopProfile.applyCategories));
                shop.setQualifications(shopProfile.qualifications);
                shop.save();
                shopProfile.setShopId(shop.id);
                shopProfile.save();
                ShopAdmin shopAdmin = ShopAdmin.find.byId(shopProfile.uid);
                if (null != shopAdmin) {
                    shopAdmin.setShopId(shop.id);
                    shopAdmin.setShopName(shop.name);
                    shopAdmin.setAdmin(true);
                    shopAdmin.save();
                }

            }
            businessUtils.addOperationLog(request, admin, "审核店铺:" + requestNode.toString());
            businessUtils.unLock(String.valueOf(id), OPERATION_AUTH_SHOP);
            return okJSON200();
        });
    }

    private String convertToArray(String categoryId) {
        String result = "[" + categoryId.replaceAll("\"", "") + "]";
        return result;
    }

}
