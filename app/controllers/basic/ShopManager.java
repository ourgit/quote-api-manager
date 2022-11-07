package controllers.basic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import constants.BusinessConstant;
import controllers.BaseAdminController;
import io.ebean.DB;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import models.admin.AdminMember;
import models.post.PostCategory;
import models.product.NewShopCategory;
import models.shop.Shop;
import models.shop.ShopApplyLog;
import models.shop.ShopProductCategory;
import models.shop.ShopTag;
import models.user.Member;
import play.Logger;
import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import utils.Pinyin4j;
import utils.ValidationUtil;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.IntStream;

import static constants.BusinessConstant.*;

/**
 *
 */
public class ShopManager extends BaseAdminController {

    Logger.ALogger logger = Logger.of(ShopManager.class);


    /**
     * @api {POST} /v1/cp/shop_list/  01门店列表
     * @apiName listShop
     * @apiGroup ADMIN-SHOP
     * @apiParam {String} filter 关键字
     * @apiParam {int} status 1为正常，2为被锁定，3为待审核 4审核不通过 5审核驳回
     * @apiParam {int} page page
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200) {JsonArray} list list
     * @apiSuccess (Success 200) {long} id id
     * @apiSuccess (Success 200){String} name 名称
     * @apiSuccess (Success 200){String} contactNumber 联系电话
     * @apiSuccess (Success 200){String} contactName 联系人
     * @apiSuccess (Success 200){String} contactAddress 联系地址
     * @apiSuccess (Success 200){String} licenseNumber 营业执照号
     * @apiSuccess (Success 200){String} licenseImg 营业执照图片
     * @apiSuccess (Success 200){String} businessTime 营业时间描述
     * @apiSuccess (Success 200){String} avatar 店铺头像
     * @apiSuccess (Success 200){String} images 店铺图片相册
     * @apiSuccess (Success 200){int} discount 折扣
     * @apiSuccess (Success 200){String} discountStr 折扣描述
     * @apiSuccess (Success 200){String} description 备注
     * @apiSuccess (Success 200){double} lat 纬度
     * @apiSuccess (Success 200){double} lon 经度
     * @apiSuccess (Success 200){long} updateTime 更新时间
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> listShop(Http.Request request) {
        return CompletableFuture.supplyAsync(() -> {
            JsonNode requestNode = request.body().asJson();
            if (null == requestNode) return okCustomJson(CODE40001, "参数错误");
            int status = requestNode.findPath("status").asInt();
            int page = requestNode.findPath("page").asInt();
            String filter = requestNode.findPath("filter").asText();

            ExpressionList<Shop> expressionList = Shop.find.query().where()
                    .ne("status", Shop.STATUS_DELETED);
            if (status > 0) expressionList.eq("status", status);
            if (!ValidationUtil.isEmpty(filter)) expressionList.icontains("filter", filter);
            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            List<Shop> list;
            if (page > 0) {
                PagedList<Shop> pagedList = expressionList.orderBy().desc("sort")
                        .orderBy().desc("id")
                        .setFirstRow((page - 1) * BusinessConstant.PAGE_SIZE_10)
                        .setMaxRows(BusinessConstant.PAGE_SIZE_10)
                        .findPagedList();
                int pages = pagedList.getTotalPageCount();
                result.put("pages", pages);
                list = pagedList.getList();
            } else {
                list = expressionList.orderBy().desc("sort")
                        .orderBy().asc("id").findList();
            }
            result.set("list", Json.toJson(list));
            return ok(result);
        });
    }

    /**
     * @api {GET} /v1/cp/shop/:id/ 02店铺详情
     * @apiName getShop
     * @apiGroup ADMIN-SHOP
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
    public CompletionStage<Result> getShop(Http.Request request, Long id) {
        return CompletableFuture.supplyAsync(() -> {
            if (id < 1) return okCustomJson(CODE40001, "参数错误");
            Shop shop = Shop.find.byId(id);
            if (null == shop) return okCustomJson(CODE40002, "该店铺不存在");
            Optional<AdminMember> optional = businessUtils.getAdminByAuthToken(request);
            if (!optional.isPresent()) return unauth403();
            AdminMember admin = optional.get();
            if (null == admin) return unauth403();
            String creatorname = "";
            if (shop.creatorId > 0) {
                Member member = Member.find.byId(shop.creatorId);
                if (null != member) {
                    creatorname = member.realName;
                    if (ValidationUtil.isEmpty(creatorname)) creatorname = member.nickName;
                }
            }
            ObjectNode result = (ObjectNode) Json.toJson(shop);
            result.put(CODE, CODE200);
            result.put("creatorname", creatorname);
            return ok(result);
        });
    }

    /**
     * @api {GET} /v1/cp/shop_apply_list/?status= 03门店申请列表
     * @apiName shopApplyList
     * @apiGroup ADMIN-SHOP
     * @apiParam {int} status
     * @apiSuccess (Success 200) {string} [idCardNo] 法人身份证号码，认证后不可修改
     * @apiSuccess (Success 200) {string} [idCardFrontUrl] 法人身份证号码正面图片地址
     * @apiSuccess (Success 200) {string} [idCardBackUrl] 法人身份证号码反面图片地址
     * @apiSuccess (Success 200) {string} [licenseUrl] 营业执照 图片地址
     * @apiSuccess (Success 200) {string} [licenseNo] 营业执照，认证后不可修改
     * @apiSuccess (Success 200) {string} [shopName] 店铺名
     * @apiSuccess (Success 200) {string} [phoneNumber] 联系电话
     * @apiSuccess (Success 200) {string} [realName] 联系人名字
     * @apiSuccess (Success 200) {string} [address] 联系地址
     * @apiSuccess (Success 200) {string} [businessItems] 经营类目
     * @apiSuccess (Success 200) {string} [images] 认证图片，可选 ，如门店照等 多张图片地址，以逗号隔开
     * @apiSuccess (Success 200) {string} [shopLink] 网店链接
     * @apiSuccess (Success 200) {string} [description] 备注
     * @apiSuccess (Success 200) {String} name 名字(法人)
     * @apiSuccess (Success 200) {String} nationality 国家
     * @apiSuccess (Success 200) {String} num 身份证号 法人
     * @apiSuccess (Success 200) {String} sex 姓别 法人
     * @apiSuccess (Success 200) {String} birth 出生 法人
     * @apiSuccess (Success 200) {String} startDate 发证起始日期 法人
     * @apiSuccess (Success 200) {String} endDate 发证结束日期 法人
     * @apiSuccess (Success 200) {String} issue 发证机构 法人
     * @apiSuccess (Success 200) {String} monthSalesAmount 月销量
     */
    public CompletionStage<Result> shopApplyList(Http.Request request, int status, int page) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<AdminMember> optional = businessUtils.getAdminByAuthToken(request);
            if (!optional.isPresent()) return unauth403();
            AdminMember admin = optional.get();
            if (null == admin) return unauth403();
            ExpressionList<ShopApplyLog> expressionList = ShopApplyLog.find.query().where();
            if (status > 0) expressionList.eq("status", status);
            PagedList<ShopApplyLog> pagedList = expressionList.orderBy().desc("id")
                    .setFirstRow((page - 1) * PAGE_SIZE_20)
                    .setMaxRows(PAGE_SIZE_20)
                    .findPagedList();
            List<ShopApplyLog> list = pagedList.getList();
            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            result.put("pages", pagedList.getTotalPageCount());
            result.set("list", Json.toJson(list));
            return ok(result);
        });
    }

    /**
     * @api {POST} /v1/cp/shop_audit/ 04审核门店
     * @apiName auditShop
     * @apiGroup ADMIN-SHOP
     * @apiParam {long} id 申请记录的ID
     * @apiParam {int} status
     * @apiParam {String} auditNote 审核说明
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> auditShop(Http.Request request) {
        return CompletableFuture.supplyAsync(() -> {
            JsonNode jsonNode = request.body().asJson();
            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            if (null == admin) return unauth403();
            long id = jsonNode.findPath("id").asLong();
            int status = jsonNode.findPath("status").asInt();
            String auditNote = jsonNode.findPath("auditNote").asText();
            ShopApplyLog log = ShopApplyLog.find.byId(id);
            if (null == log) return okCustomJson(CODE40001, "该申请单不存在");
            if (log.status >= ShopApplyLog.STATUS_AUDIT_PASS) {
                return okCustomJson(CODE40001, "已审核");
            }
            if (!businessUtils.setLock(String.valueOf(id), OPERATION_AUDIT_STORE))
                return okCustomJson(CODE40004, "正在操作中，请稍等");
            log.setStatus(status);
            log.setAuditorName(admin.realName);
            log.setAuditorUid(admin.id);
            log.setAuditNote(auditNote);
            log.setAuditTime(dateUtils.getCurrentTimeBySecond());
            log.save();
            if (status == ShopApplyLog.STATUS_AUDIT_PASS) {
                Shop shop = new Shop();
                shop.setStatus(Shop.STATUS_NORMAL);
                shop.setName(log.shopName);
                shop.setDigest(log.digest);
                shop.setContactNumber(log.phoneNumber);
                shop.setContactName(log.userName);
                shop.setContactAddress(log.address);
                shop.setDescription("");
                shop.setBusinessTime("");
                shop.setApproveNote("");
                shop.setRectLogo(log.logo);
                shop.setAvatar(log.logo);
                shop.setApplyCategories(log.categoryList);
                if (!ValidationUtil.isEmpty(log.categoryList)) {
                    ArrayNode nodes = (ArrayNode) Json.parse(log.categoryList);
                    StringBuilder sb = new StringBuilder();
                    nodes.forEach((node) -> {
                        long categoryId = node.asLong();
                        PostCategory postCategory = PostCategory.find.byId(categoryId);
                        if (null != postCategory) {
                            sb.append(postCategory.name);
                        }
                    });
                    shop.setApplyCategoriesName(sb.toString());
                }
                long currentTime = dateUtils.getCurrentTimeBySecond();
                shop.setUpdateTime(currentTime);
                shop.setCreateTime(currentTime);
                shop.setCreatorId(log.uid);
                shop.setApproverId(admin.id);
                shop.setApproveNote(auditNote);
                shop.setFilter(Json.stringify(Json.toJson(shop)));
                shop.save();
                Member member = Member.find.byId(log.uid);
                if (null != member) {
                    member.setAuthStatus(Member.AUTH_STATUS_AUTHED);
                    member.setShopId(shop.id);
                    member.setShopName(shop.name);
                    member.setUserType(Member.USER_TYPE_MANAGER);
                    member.setRealName(log.userName);
                    member.save();
                }
            }
            businessUtils.unLock(String.valueOf(id), OPERATION_AUDIT_STORE);
            return okJSON200();
        });
    }

    /**
     * @api {POST} /v1/cp/shop/:id/ 05修改门店
     * @apiName updateShop
     * @apiGroup ADMIN-SHOP
     * @apiParam {String} name 名称
     * @apiParam {String} contactNumber 联系电话
     * @apiParam {String} contactName 联系人
     * @apiParam {String} contactAddress 联系地址
     * @apiParam {String} licenseNumber 营业执照号
     * @apiParam {String} licenseImg 营业执照图片
     * @apiParam {String} description 备注
     * @apiParam {double} lat 纬度
     * @apiParam {double} lon 经度
     * @apiParam {boolean} asSelfTakenPlace 做为自取点
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> updateShop(Http.Request request, long id) {
        JsonNode requestNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            if (id < 1) return okCustomJson(CODE40001, "参数错误");
            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            if (null == admin) return unauth403();
            Shop shop = Shop.find.byId(id);
            if (null == shop) return okCustomJson(CODE40002, "该店铺不存在");
            Shop param = Json.fromJson(requestNode, Shop.class);
            if (null == param) return okCustomJson(CODE40001, "参数错误");
            if (!ValidationUtil.isEmpty(param.name)) {
                Shop nameOrg = Shop.find.query().where()
                        .eq("name", param.name)
                        .ne("id", shop.id)
                        .setMaxRows(1).findOne();
                if (null != nameOrg) return okCustomJson(CODE40001, "该店铺已存在");
                shop.setName(param.name);
            }
            if (param.status > 0) shop.setStatus(param.status);
            if (!ValidationUtil.isEmpty(param.name)) {
                shop.setName(param.name);
            }
            if (!ValidationUtil.isEmpty(param.rectLogo)) shop.setRectLogo(param.rectLogo);
            if (!ValidationUtil.isEmpty(param.contactNumber)) shop.setContactNumber(param.contactNumber);
            if (!ValidationUtil.isEmpty(param.contactName)) shop.setContactName(param.contactName);
            if (!ValidationUtil.isEmpty(param.contactAddress)) shop.setContactAddress(param.contactAddress);
            if (!ValidationUtil.isEmpty(param.licenseNumber)) shop.setLicenseNumber(param.licenseNumber);
            if (!ValidationUtil.isEmpty(param.licenseImg)) shop.setLicenseImg(param.licenseImg);
            if (!ValidationUtil.isEmpty(param.description)) shop.setDescription(param.description);
            if (!ValidationUtil.isEmpty(param.approveNote)) shop.setApproveNote(param.approveNote);
            if (requestNode.has("productCounts")) shop.setProductCounts(param.productCounts);
            if (requestNode.has("views")) shop.setViews(param.views);
            if (requestNode.has("digest")) shop.setDigest(param.digest);
            if (requestNode.has("placeTop")) shop.setPlaceTop(param.placeTop);
            if (requestNode.has("sort")) shop.setSort(param.sort);
            if (requestNode.has("shopLevel")) shop.setShopLevel(param.shopLevel);
            if (requestNode.has("envImages")) shop.setEnvImages(param.envImages);
            if (requestNode.has("bulletin")) shop.setBulletin(param.bulletin);
            if (requestNode.has("creatorId")) {
                shop.setCreatorId(param.creatorId);
            }
            if (requestNode.has("businessTime")) {
                shop.setBusinessTime(param.businessTime);
            }
            if (requestNode.has("avatar")) {
                shop.setAvatar(param.avatar);
            }
            if (requestNode.has("images")) {
                shop.setImages(param.images);
            }

            if (requestNode.has("averageConsumption")) {
                shop.setAverageConsumption(param.averageConsumption);
            }
            if (requestNode.has("orderCount")) {
                shop.setOrderCount(param.orderCount);
            }
            if (!ValidationUtil.isEmpty(param.log)) shop.setLog(param.log);
            if (requestNode.has("lat")) {
                double lat = param.lat;
                shop.setLat(lat);
            }
            if (requestNode.has("lon")) {
                double lon = param.lon;
                shop.setLon(lon);
            }
            if (requestNode.has("openTime")) {
                shop.setOpenTime(requestNode.findPath("openTime").asInt());
            }
            if (requestNode.has("closeTime")) {
                shop.setCloseTime(requestNode.findPath("closeTime").asInt());
            }
            if (requestNode.has("productCounts")) shop.setProductCounts(param.productCounts);
            if (requestNode.has("views")) shop.setViews(param.views);
            if (requestNode.has("tags") && !ValidationUtil.isEmpty(param.tags) && !param.tags.equalsIgnoreCase(shop.tags)) {
                shop.setTags(param.tags);
                updateShopTag(param.tags, shop.id);
            }
            shop.setUpdateTime(dateUtils.getCurrentTimeBySecond());
            shop.setFilter("");
            shop.setFilter(Json.stringify(Json.toJson(shop)));
            shop.save();
            businessUtils.addOperationLog(request, admin, "修改店铺：" + requestNode.toString());
            return okJSON200();
        });
    }

    /**
     * @api {POST} /v1/cp/shop/ 05删除门店
     * @apiName deleteShop
     * @apiGroup ADMIN-SHOP
     * @apiParam {int} id 店铺id
     * @apiParam {String} operation 操作,"del"为删除
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> deleteShop(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        String operation = jsonNode.findPath("operation").asText();
        return CompletableFuture.supplyAsync(() -> {
            if (ValidationUtil.isEmpty(operation) || !operation.equals("del"))
                return okCustomJson(CODE40001, "参数错误");
            long id = jsonNode.findPath("id").asLong();
            if (id < 1) return okCustomJson(CODE40001, "参数错误");
            Shop store = Shop.find.byId(id);
            if (null == store) return okCustomJson(CODE40001, "该店铺不存在");
            store.setStatus(Shop.STATUS_DELETED);
            store.save();

            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            businessUtils.addOperationLog(request, admin, "删除门店：" + jsonNode.toString());
            return okJSON200();
        });
    }

    private void updateShopTag(String tag, long shopId) {
        List<ShopTag> tags = ShopTag.find.query().where().eq("shopId", shopId).findList();
        if (tags.size() > 0) {
            DB.deleteAll(tags);
            syncCache.remove(cacheUtils.getShopsByTagJsonCache(tag));
        }
        String[] tagList = tag.split(",");
        if (null != tagList) {
            List<ShopTag> list = new ArrayList<>();
            Arrays.stream(tagList).forEach((each) -> {
                ShopTag productTag = new ShopTag();
                productTag.setShopId(shopId);
                productTag.setTag(each);
                list.add(productTag);
            });
            if (list.size() > 0) DB.saveAll(list);
        }

    }


}
