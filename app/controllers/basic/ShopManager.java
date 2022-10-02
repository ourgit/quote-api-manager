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

import static constants.BusinessConstant.OPERATION_TRANSFER_TO_BALANCE;
import static constants.BusinessConstant.PAGE_SIZE_20;

/**
 *
 */
public class ShopManager extends BaseAdminController {

    Logger.ALogger logger = Logger.of(ShopManager.class);

    @Inject
    Pinyin4j pinyin4j;
    /**
     * @api {POST} /v1/cp/shop_list/  01门店列表
     * @apiName listStore
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
     * @api {POST} /v1/cp/store_audit/ 04审核门店
     * @apiName auditStore
     * @apiGroup ADMIN-SHOP
     * @apiParam {long} id 申请记录的ID
     * @apiParam {int} status
     * @apiParam {String} auditNote 审核说明
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> auditStore(Http.Request request) {
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
            if (!businessUtils.setLock(String.valueOf(id), OPERATION_TRANSFER_TO_BALANCE))
                return okCustomJson(CODE40004, "正在操作中，请稍等");
            log.setStatus(status);
            log.setAuditorName(admin.realName);
            log.setAuditorUid(admin.id);
            log.setAuditNote(auditNote);
            log.setAuditTime(dateUtils.getCurrentTimeBySecond());
            log.save();
            if (status == ShopApplyLog.STATUS_AUDIT_PASS) {
                Shop store = new Shop();
                store.setStatus(Shop.STATUS_NORMAL);
                store.setName(log.shopName);
                store.setDigest("");
                store.setContactNumber(log.phoneNumber);
                store.setContactName(log.userName);
                store.setContactAddress(log.address);
                store.setLicenseNumber(log.licenseNo);
                store.setLicenseImg(log.licenseUrl);
                store.setDescription("");
                store.setApproveNote("");
                store.setRectLogo(log.logo);
                store.setAvatar(log.logo);
                store.setFilter(Json.stringify(Json.toJson(store)));
                store.save();
                Member member = Member.find.byId(log.uid);
                if (null != member) {
                    member.setAuthStatus(Member.AUTH_STATUS_AUTHED);
                    member.setShopId(store.id);
                    member.setShopName(store.name);
                    member.setUserType(Member.USER_TYPE_MANAGER);
                    member.setRealName(log.userName);
                    member.save();
                }
            }
            businessUtils.unLock(String.valueOf(id), OPERATION_TRANSFER_TO_BALANCE);
            return okJSON200();
        });
    }

    /**
     * @api {POST} /v1/cp/store/:id/ 05修改门店
     * @apiName updateStore
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
    public CompletionStage<Result> updateStore(Http.Request request, long id) {
        JsonNode requestNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            if (id < 1) return okCustomJson(CODE40001, "参数错误");
            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            if (null == admin) return unauth403();
            Store shop = Store.find.byId(id);
            if (null == shop) return okCustomJson(CODE40002, "该店铺不存在");
            Store param = Json.fromJson(requestNode, Store.class);
            if (null == param) return okCustomJson(CODE40001, "参数错误");
            if (!ValidationUtil.isEmpty(param.name)) {
                Store nameOrg = Store.find.query().where()
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
            shop.setUpdateTime(dateUtils.getCurrentTimeBySecond());
            shop.setFilter("");
            shop.setFilter(Json.stringify(Json.toJson(shop)));
            shop.save();
            businessUtils.addOperationLog(request, admin, "修改店铺：" + requestNode.toString());
            return okJSON200();
        });
    }

    /**
     * @api {POST} /v1/cp/store/ 05删除门店
     * @apiName deleteStore
     * @apiGroup ADMIN-SHOP
     * @apiParam {int} id 店铺id
     * @apiParam {String} operation 操作,"del"为删除
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> deleteStore(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        String operation = jsonNode.findPath("operation").asText();
        return CompletableFuture.supplyAsync(() -> {
            if (ValidationUtil.isEmpty(operation) || !operation.equals("del"))
                return okCustomJson(CODE40001, "参数错误");
            long id = jsonNode.findPath("id").asLong();
            if (id < 1) return okCustomJson(CODE40001, "参数错误");
            Store store = Store.find.byId(id);
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

    /**
     * @api {POST} /v1/cp/shops/  01店铺列表
     * @apiName listOrg
     * @apiGroup ADMIN-SHOP
     * @apiParam {String} filter 关键字
     * @apiParam {int} runType 1自营 2第三方
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
    public CompletionStage<Result> listOrg(Http.Request request) {
        return CompletableFuture.supplyAsync(() -> {
            JsonNode requestNode = request.body().asJson();
            if (null == requestNode) return okCustomJson(CODE40001, "参数错误");
            int runType = requestNode.findPath("runType").asInt();
            int status = requestNode.findPath("status").asInt();
            int page = requestNode.findPath("page").asInt();
            String filter = requestNode.findPath("filter").asText();

            String includeCategoryIdList = requestNode.findPath("includeCategoryIdList").asText();
            String excludeCategoryIdList = requestNode.findPath("excludeCategoryIdList").asText();

            ExpressionList<Shop> expressionList = Shop.find.query().where().ne("status", Shop.STATUS_DELETED);
            if (runType > 0) expressionList.eq("runType", runType);
            if (status > 0) expressionList.eq("status", status);
            if (!ValidationUtil.isEmpty(filter)) expressionList.icontains("filter", filter);

            List<Shop> shopList = Shop.find.query().where().eq("status", Shop.STATUS_NORMAL)
                    .findList();
            if (requestNode.has("includeCategoryIdList") && !ValidationUtil.isEmpty(includeCategoryIdList)) {
                ArrayNode nodes = (ArrayNode) Json.parse(includeCategoryIdList);
                if (nodes.size() > 0) {
                    List<Long> subCategoryList = buildCategoryIdList(nodes);
                    Set<Integer> includeShopIdList = new HashSet<>();
                    for (Shop shop : shopList) {
                        for (Long id : subCategoryList) {
                            if (shop.newShopCategoryId.contains("" + id)) {
                                includeShopIdList.add(shop.id);
                            }
                        }
                    }
                    if (includeShopIdList.size() < 1) {
                        includeShopIdList.add(0);
                    }
                    expressionList.idIn(includeShopIdList);
                }
            }
            if (requestNode.has("excludeCategoryIdList") && !ValidationUtil.isEmpty(excludeCategoryIdList)) {
                ArrayNode nodes = (ArrayNode) Json.parse(excludeCategoryIdList);
                if (nodes.size() > 0) {
                    List<Long> subCategoryList = buildCategoryIdList(nodes);
                    Set<Integer> allIdList = new HashSet<>();
                    shopList.parallelStream().forEach((each) -> allIdList.add(each.id));
                    Set<Integer> excludeShopIdList = new HashSet<>();
                    for (Long id : subCategoryList) {
                        for (Shop shop : shopList) {
                            if (shop.newShopCategoryId.contains("" + id)) {
                                excludeShopIdList.add(shop.id);
                            }
                        }
                    }
                    allIdList.removeAll(excludeShopIdList);
                    if (allIdList.size() < 1) {
                        allIdList.add(0);
                    }
                    expressionList.idIn(allIdList);
                }
            }
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
            list.parallelStream().forEach((each) -> {
                if (each.creatorId > 0) {
                    Member member = Member.find.byId(each.creatorId);
                    if (null != member) {
                        String name = member.realName;
                        if (ValidationUtil.isEmpty(name)) name = member.nickName;
                        each.customerName = name;
                    }
                }
            });
            result.set("list", Json.toJson(list));
            return ok(result);
        });
    }

    public List<Long> buildCategoryIdList(ArrayNode nodes) {
        List<Long> subCategoryList = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            long categoryId = nodes.get(i).asLong();
            if (categoryId > 0) {
                NewShopCategory category = NewShopCategory.find.byId(categoryId);
                if (null != category) {
                    List<Long> subList = NewShopCategory.find.query().where()
                            .icontains("path", "/" + category.id + "/")
                            .findIds();
                    subCategoryList.add(categoryId);
                    subCategoryList.addAll(subList);
                }
            }
        }
        return subCategoryList;
    }

    /**
     * @api {GET} /v1/cp/shop/:id/ 02店铺详情
     * @apiName getOrg
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
    public CompletionStage<Result> getOrg(Http.Request request, Long id) {
        return CompletableFuture.supplyAsync(() -> {
            if (id < 1) return okCustomJson(CODE40001, "参数错误");
            Shop org = Shop.find.byId(id);
            if (null == org) return okCustomJson(CODE40002, "该店铺不存在");
            Optional<AdminMember> optional = businessUtils.getAdminByAuthToken(request);
            if (!optional.isPresent()) return unauth403();
            AdminMember admin = optional.get();
            if (null == admin) return unauth403();
            String creatorname = "";
            if (org.creatorId > 0) {
                Member member = Member.find.byId(org.creatorId);
                if (null != member) {
                    creatorname = member.realName;
                    if (ValidationUtil.isEmpty(creatorname)) creatorname = member.nickName;
                }
            }
            ObjectNode result = (ObjectNode) Json.toJson(org);
            result.put(CODE, CODE200);
            result.put("creatorname", creatorname);
            return ok(result);
        });
    }

    /**
     * @api {POST} /v1/cp/shop/new/ 03添加店铺
     * @apiName addOrg
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
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> addOrg(Http.Request request) {
        JsonNode requestNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            if (null == admin) return unauth403();
            Shop param = Json.fromJson(requestNode, Shop.class);
            if (ValidationUtil.isEmpty(param.name)) return okCustomJson(CODE40001, "请输入店铺名字");
            if (param.name.length() > 50) return okCustomJson(CODE40001, "店铺名字最长50个字符");
            Shop nameOrg = Shop.find.query().where().eq("name", param.name).setMaxRows(1).findOne();
            if (null != nameOrg) return okCustomJson(CODE40001, "该店铺已存在");
            param.setCreateTime(dateUtils.getCurrentTimeBySecond());
            param.setApproverId(admin.id);
            param.setFilter(param.toString());
            param.setStatus(Shop.STATUS_NORMAL);
            if (param.discount > 100) return okCustomJson(CODE40001, "折扣点有误，请检查");
            if (param.bidDiscount > 100) return okCustomJson(CODE40001, "进价折扣点有误，请检查");
            param.save();
            if (!ValidationUtil.isEmpty(param.tags)) {
                updateShopTag(param.tags, param.id);
            }
            businessUtils.addOperationLog(request, admin, "添加店铺：" + requestNode.toString());
            ObjectNode resultNode = (ObjectNode) Json.toJson(param);
            resultNode.put(CODE, CODE200);
            updateCache();
            return ok(resultNode);
        });
    }

    private void updateCache() {
        IntStream.range(1, 20).forEach((page) -> {
            String key = cacheUtils.getShopListJsonCache(page);
            asyncCache.remove(key);
        });
        String key = cacheUtils.getTopShopListJsonCache();
        asyncCache.remove(key);
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

    /**
     * @api {POST} /v1/cp/shop/:id/ 04修改店铺
     * @apiName updateOrg
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
    public CompletionStage<Result> updateOrg(Http.Request request, long id) {
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
            if (param.runType > 0) shop.setRunType(param.runType);

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
            if (requestNode.has("tags") && !ValidationUtil.isEmpty(param.tags) && !param.tags.equalsIgnoreCase(shop.tags)) {
                shop.setTags(param.tags);
                updateShopTag(param.tags, shop.id);
            }
            if (requestNode.has("digest")) shop.setDigest(param.digest);
            if (requestNode.has("dadaShopId")) shop.setDadaShopId(param.dadaShopId);
            if (requestNode.has("dadaUserNo")) shop.setDadaUserNo(param.dadaUserNo);
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
            if (requestNode.has("discountStr")) {
                shop.setDiscountStr(param.discountStr);
            }
            if (requestNode.has("discount")) {
                if (param.discount > 100) return okCustomJson(CODE40001, "折扣点有误，请检查");
                shop.setDiscount(param.discount);
            }
            if (requestNode.has("bidDiscount")) {
                if (param.bidDiscount > 100) return okCustomJson(CODE40001, "进价折扣点有误，请检查");
                shop.setBidDiscount(param.bidDiscount);
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
            if (requestNode.has("branches")) {
                shop.setBranches(requestNode.findPath("branches").asText());
            }
            if (requestNode.has("applyCategories")) {
                shop.setApplyCategories(requestNode.findPath("applyCategories").asText());
            }
            if (requestNode.has("applyCategoriesName")) {
                shop.setApplyCategoriesName(requestNode.findPath("applyCategoriesName").asText());
            }
            shop.setUpdateTime(dateUtils.getCurrentTimeBySecond());
            shop.setFilter("");
            shop.setFilter(Json.stringify(Json.toJson(shop)));
            shop.save();
            businessUtils.addOperationLog(request, admin, "修改店铺：" + requestNode.toString());
            updateCache();
            return okJSON200();
        });
    }

    /**
     * @api {POST} /v1/cp/shop/ 05删除店铺
     * @apiName deleteOrg
     * @apiGroup ADMIN-SHOP
     * @apiParam {int} id 店铺id
     * @apiParam {String} operation 操作,"del"为删除
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> deleteOrg(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        String operation = jsonNode.findPath("operation").asText();
        return CompletableFuture.supplyAsync(() -> {
            if (ValidationUtil.isEmpty(operation) || !operation.equals("del")) return okCustomJson(CODE40001, "参数错误");
            long id = jsonNode.findPath("id").asLong();
            if (id < 1) return okCustomJson(CODE40001, "参数错误");
            Shop org = Shop.find.byId(id);
            if (null == org) return okCustomJson(CODE40001, "该店铺不存在");
            org.setStatus(Shop.STATUS_DELETED);
            org.save();

            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            businessUtils.addOperationLog(request, admin, "删除店铺：" + jsonNode.toString());
            updateCache();
            return okJSON200();
        });
    }

    /**
     * @api {POST} /v1/cp/batch_update_org_member/:orgId/ 06批量修改店铺成员
     * @apiName batchUpdateOrgMember
     * @apiGroup ADMIN-SHOP
     * @apiParam {JsonArray} list memberId的数组
     * @apiSuccess (Success 200){int} code 200
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> batchUpdateOrgMember(Http.Request request, long shopId) {
        JsonNode jsonNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            if (null == jsonNode) return okCustomJson(CODE40001, "参数错误");
            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            if (null == admin) return unauth403();
            Shop org = Shop.find.byId(shopId);
            if (null == org) return okCustomJson(CODE40002, "该店铺不存在");
            ArrayNode list = (ArrayNode) jsonNode.findPath("list");
            if (null != list) {
                List<AdminMember> members = AdminMember.find.query().where().eq("shopId", shopId).findList();
                members.parallelStream().forEach((each) -> {
                    each.setShopId(0);
                    each.setShopName("");
                });
                DB.saveAll(members);
                List<AdminMember> orgMembers = new ArrayList<>();
                if (list.size() > 0) {
                    list.forEach((node) -> {
                        long memberId = node.asLong();
                        AdminMember adminMember = AdminMember.find.byId(memberId);
                        if (null != adminMember) {
                            adminMember.setShopId(shopId);
                            adminMember.setShopName(org.name);
                            orgMembers.add(adminMember);
                        }
                    });
                    if (orgMembers.size() > 0) {
                        DB.saveAll(orgMembers);
                        org.save();
                    }
                }
            }
            businessUtils.addOperationLog(request, admin, "批量修改店铺成员：" + jsonNode.toString());
            return okJSON200();
        });
    }


    /**
     * @api {GET} /v1/cp/shop_members/?orgId=  07店铺成员列表
     * @apiName listOrgMembers
     * @apiGroup ADMIN-SHOP
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200) {JsonArray} list 成员列表
     */
    public CompletionStage<Result> listOrgMembers(Http.Request request, final long orgId) {
        return CompletableFuture.supplyAsync(() -> {
            long queryOrgId = orgId;
            Optional<AdminMember> optional = businessUtils.getAdminByAuthToken(request);
            if (!optional.isPresent()) return unauth403();
            AdminMember admin = optional.get();
            if (null == admin) return unauth403();
            if (!businessUtils.isAdmin(admin)) {
                queryOrgId = admin.shopId;
            }
            List<AdminMember> list = AdminMember.find.query().where()
                    .eq("shopId", queryOrgId)
                    .orderBy().asc("id").findList();
            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            result.set("list", Json.toJson(list));
            return ok(result);
        });
    }


    /**
     * @api {POST} /v1/cp/bind_shop/ 08会员绑定店铺
     * @apiName bindShop
     * @apiGroup ADMIN-SHOP
     * @apiParam {long} uid 用户UID
     * @apiParam {long} shopId 店铺ID
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> bindShop(Http.Request request) {
        JsonNode requestNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            if (null == admin) return unauth403();
            long uid = requestNode.get("uid").asLong();
            long shopId = requestNode.get("shopId").asLong();
            Shop shop = Shop.find.byId(shopId);
            if (null == shop) return okCustomJson(CODE40001, "该店铺已不在");
            Member member = Member.find.byId(uid);
            if (null == member) return okCustomJson(CODE40001, "该用户不存在");
            member.setShopId(shopId);
            member.save();
            businessUtils.addOperationLog(request, admin, "会员绑定店铺：" + requestNode.toString());
            return okJSON200();
        });
    }

    /**
     * @api {GET} /v1/cp/shop_product_categories/?page=&filter=&shopId=&parentId= 09店铺商品分类列表
     * @apiName listShopProductCategories
     * @apiGroup ADMIN-SHOP
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200) {int} pages 页数
     * @apiSuccess (Success 200) {JsonArray} list 列表
     * @apiSuccess (Success 200){long} id 分类id
     * @apiSuccess (Success 200){long} parentId 父类id
     * @apiSuccess (Success 200){boolean} isParent 该类目是否为父类目，1为true，0为false
     * @apiSuccess (Success 200){String} name 名称
     * @apiSuccess (Success 200){int} show 1显示2不显示
     * @apiSuccess (Success 200){int} sort 排序顺序
     * @apiSuccess (Success 200){JsonArray} children 子列表
     * @apiSuccess (Success 200){string} updateTime 更新时间
     */
    public CompletionStage<Result> listShopProductCategories(final String filter, long shopId, long parentId) {
        return CompletableFuture.supplyAsync(() -> {
            ExpressionList<ShopProductCategory> expressionList = ShopProductCategory.find.query().where().eq("shopId", shopId);
            if (!ValidationUtil.isEmpty(filter)) expressionList.icontains("name", filter);
            if (parentId >= 0) expressionList.eq("parentId", parentId);
            List<ShopProductCategory> list = expressionList.orderBy().desc("sort").findList();
            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            List<ShopProductCategory> resultList = convertListToTreeNode(list);
            result.set("list", Json.toJson(resultList));
            return ok(result);
        });
    }

    public List<ShopProductCategory> convertListToTreeNode(List<ShopProductCategory> categoryList) {
        List<ShopProductCategory> nodeList = new ArrayList<>();
        if (null == categoryList) return nodeList;
        for (ShopProductCategory node1 : categoryList) {
            boolean mark = false;
            for (ShopProductCategory node2 : categoryList) {
                if (node1.parentId == node2.id) {
                    mark = true;
                    if (node2.children == null)
                        node2.children = new ArrayList<>();
                    node2.children.add(node1);
                    break;
                }
            }
            if (!mark) {
                nodeList.add(node1);
            }
        }
        return nodeList;
    }

    /**
     * @api {POST} /v1/cp/shop_product_categories/new/ 10添加店铺商品分类
     * @apiName addShopProductCategory
     * @apiGroup ADMIN-SHOP
     * @apiParam {String} name 名称
     * @apiParam {String} imgUrl 图片地址
     * @apiParam {String} poster 海报图片地址
     * @apiParam {int} show 1显示2不显示
     * @apiParam {long} parentId 父类id
     * @apiParam {int} sort 排序顺序
     * @apiSuccess (Success 200) {int} code 200 请求成功
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> addShopProductCategory(Http.Request request) {
        JsonNode requestNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            String name = requestNode.findPath("name").asText();
            int show = requestNode.findPath("show").asInt();
            long shopId = requestNode.findPath("shopId").asLong();
            long parentId = requestNode.findPath("parentId").asLong();
            int sort = requestNode.findPath("sort").asInt();
            String imgUrl = requestNode.findPath("imgUrl").asText();
            String poster = requestNode.findPath("poster").asText();
            Shop shop = Shop.find.byId(shopId);
            if (null == shop) return okCustomJson(CODE40001, "该店铺不存在");
            if (ValidationUtil.isEmpty(name)) return okCustomJson(CODE40001, "参数错误");
            ShopProductCategory parentMerchantCategory = null;
            if (parentId > 0) {
                parentMerchantCategory = ShopProductCategory.find.byId(parentId);
                if (null == parentMerchantCategory) return okCustomJson(CODE40001, "父类ID不存在");
            }
            if (show < 1) show = 1;
            ShopProductCategory category = new ShopProductCategory();
            category.setShopId(shopId);
            category.setName(name);
            category.setPinyinAbbr(pinyin4j.toPinYinUppercase(name));
            category.setShow(show);
            category.setSort(sort);
            category.setParentId(parentId);
            category.setPoster(poster);
            if (null != parentMerchantCategory) {
                String parentPath = parentMerchantCategory.path;
                if (ValidationUtil.isEmpty(parentPath)) parentPath = "/";
                category.setPath(parentPath + parentMerchantCategory.id + "/");
            } else category.setPath("/");

            long currentTime = dateUtils.getCurrentTimeBySecond();
            category.setCreateTime(currentTime);
            if (!ValidationUtil.isEmpty(imgUrl)) category.setImgUrl(imgUrl);
            category.save();
            updateCategoryCache(shopId);
            return okJSON200();
        });
    }

    /**
     * @api {POST} /v1/cp/shop_product_categories/:categoryId/ 11修改店铺商品分类
     * @apiName updateShopProductCategory
     * @apiGroup ADMIN-SHOP
     * @apiParam {String} name 名称
     * @apiParam {String} imgUrl 图片地址
     * @apiParam {String} poster 海报地址
     * @apiParam {int} show 1显示2不显示
     * @apiParam {long} parentId 父类id
     * @apiParam {int} sort 排序顺序
     * @apiSuccess (Success 200) {int} code 200 请求成功
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> updateShopProductCategory(Http.Request request, long categoryId) {
        JsonNode requestNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            if (categoryId < 1) return okCustomJson(CODE40001, "参数错误");
            ShopProductCategory category = ShopProductCategory.find.byId(categoryId);
            if (null == category) return okCustomJson(CODE40002, "该商品分类不存在");
            long parentId = requestNode.findPath("parentId").asLong();
            if (categoryId == parentId) return okCustomJson(CODE40002, "子分类不能跟父分类一样");
            String name = requestNode.findPath("name").asText();
            int show = requestNode.findPath("show").asInt();
            int sort = requestNode.findPath("sort").asInt();
            if (!ValidationUtil.isEmpty(name)) {
                category.setName(name);
                category.setPinyinAbbr(pinyin4j.toPinYinUppercase(name));
            }
            if (show > 0) {
                category.setShow(show);
                if (show == PostCategory.HIDE_CATEGORY) {
                    List<ShopProductCategory> list = ShopProductCategory.find.query().where().icontains("path", "/" + category.id + "/").findList();
                    list.parallelStream().forEach((each) -> {
                        each.setShow(PostCategory.HIDE_CATEGORY);
                    });
                    DB.saveAll(list);
                }
            }
            if (sort > 0) category.setSort(sort);
            setCategory(category, parentId);
            String imgUrl = requestNode.findPath("imgUrl").asText();
            if (!ValidationUtil.isEmpty(imgUrl)) category.setImgUrl(imgUrl);
            String poster = requestNode.findPath("poster").asText();
            if (!ValidationUtil.isEmpty(poster)) category.setPoster(poster);
            category.save();
            updateCategoryCache(category.shopId);
            return okJSON200();
        });
    }

    private void setCategory(ShopProductCategory category, long parentId) {
        if (parentId > -1) {
            category.setParentId(parentId);
            if (parentId > 0) {
                ShopProductCategory parentMerchantCategory = ShopProductCategory.find.byId(parentId);
                if (null != parentMerchantCategory) {
                    String parentPath = parentMerchantCategory.path;
                    if (ValidationUtil.isEmpty(parentPath)) parentPath = "/";
                    category.setPath(parentPath + parentMerchantCategory.id + "/");
                }
            } else category.setPath("/");
        }
    }

    /**
     * @api {POST} /v1/cp/shop_product_categories/ 12删除店铺商品分类
     * @apiName deleteShopProductCategory
     * @apiGroup ADMIN-SHOP
     * @apiParam {int} categoryId 商品分类id
     * @apiParam {String} operation 操作,"del"为删除
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     * @apiSuccess (Error 40002) {int} code 40002 该分类不存在
     * @apiSuccess (Error 40003) {int} code 40003 该分类为父级分类,不能直接删除
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> deleteShopProductCategory(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        String operation = jsonNode.findPath("operation").asText();
        return CompletableFuture.supplyAsync(() -> {
            if (ValidationUtil.isEmpty(operation) || !operation.equals("del")) return okCustomJson(CODE40001, "参数错误");
            long categoryId = jsonNode.findPath("categoryId").asInt();
            if (categoryId < 1) return okCustomJson(CODE40001, "参数错误");
            ShopProductCategory category = ShopProductCategory.find.byId(categoryId);
            ShopProductCategory subCategories = ShopProductCategory.find.query().where().eq("parentId", category.id).setMaxRows(1).findOne();
            if (null != subCategories) return okCustomJson(CODE40003, "该分类为父级分类,不能直接删除");
            long shopId = category.shopId;
            category.delete();
            updateCategoryCache(shopId);
            return okJSON200();
        });
    }

    private void updateCategoryCache(long shopId) {
        cacheUtils.updateShopProductCategoryCache(shopId);
    }

}
