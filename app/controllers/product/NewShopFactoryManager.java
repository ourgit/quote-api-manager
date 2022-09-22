package controllers.product;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.BaseAdminSecurityController;
import io.ebean.DB;
import io.ebean.ExpressionList;
import models.product.NewShopCategory;
import models.shop.NewShopFactory;
import models.shop.Shop;
import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import utils.Pinyin4j;
import utils.ValidationUtil;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


/**
 * 商品管理
 */
public class NewShopFactoryManager extends BaseAdminSecurityController {
    @Inject
    Pinyin4j pinyin4j;

    /**
     * @api {GET} /v1/cp/new_shop_factories/?page=&filter=&parentId= 01新店工厂分类列表
     * @apiName listNewShopFactories
     * @apiGroup ADMIN-NEW-SHOP-FACTORY
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
    public CompletionStage<Result> listNewShopFactories(final String filter, long parentId) {
        return CompletableFuture.supplyAsync(() -> {
            ExpressionList<NewShopFactory> expressionList = NewShopFactory.find.query().where();
            if (!ValidationUtil.isEmpty(filter)) expressionList.icontains("name", filter);
            if (parentId >= 0) expressionList.eq("parentId", parentId);
            List<NewShopFactory> list = expressionList.orderBy().desc("sort").findList();
            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            List<NewShopFactory> resultList = businessUtils.convertFactoryListToTreeNode(list);
            result.set("list", Json.toJson(resultList));
            return ok(result);
        });
    }

    /**
     * @api {GET} /v1/cp/new_shop_factories/:newShopFactoryId/ 02新店工厂分类详情
     * @apiName getNewShopFactory
     * @apiGroup ADMIN-NEW-SHOP-FACTORY
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200){long} id 分类id
     * @apiSuccess (Success 200){String} name 名称
     * @apiSuccess (Success 200){int} show 1显示2不显示
     * @apiSuccess (Success 200){long} parentId 父类id
     * @apiSuccess (Success 200){int} sort 排序顺序
     * @apiSuccess (Success 200){JsonArray} children 子列表
     * @apiSuccess (Success 200){string} updateTime 更新时间
     */
    public CompletionStage<Result> getNewShopFactory(long newShopFactoryId) {
        return CompletableFuture.supplyAsync(() -> {
            if (newShopFactoryId < 1) return okCustomJson(CODE40001, "参数错误");
            NewShopFactory newShopFactory = NewShopFactory.find.byId(newShopFactoryId);
            if (null == newShopFactory) return okCustomJson(CODE40002, "该商品分类不存在");
            List<NewShopFactory> children = NewShopFactory.find.query().where()
                    .eq("parentId", newShopFactoryId)
                    .findList();
            newShopFactory.children = children;
            ObjectNode result = (ObjectNode) Json.toJson(newShopFactory);
            result.put(CODE, CODE200);
            return ok(result);
        });
    }

    /**
     * @api {POST} /v1/cp/new_shop_factories/new/ 03添加新店工厂分类
     * @apiName addNewShopFactory
     * @apiGroup ADMIN-NEW-SHOP-FACTORY
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
    public CompletionStage<Result> addNewShopFactory(Http.Request request) {
        JsonNode requestNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            String name = requestNode.findPath("name").asText();
            int show = requestNode.findPath("show").asInt();
            long parentId = requestNode.findPath("parentId").asLong();
            int sort = requestNode.findPath("sort").asInt();
            String imgUrl = requestNode.findPath("imgUrl").asText();
            String poster = requestNode.findPath("poster").asText();
            if (ValidationUtil.isEmpty(name)) return okCustomJson(CODE40001, "参数错误");
            NewShopFactory parentMerchantNewShopCategory = null;
            if (parentId > 0) {
                parentMerchantNewShopCategory = NewShopFactory.find.byId(parentId);
                if (null == parentMerchantNewShopCategory) return okCustomJson(CODE40001, "父类ID不存在");
            }
            if (show < 1) show = 1;
            NewShopFactory newShopFactory = new NewShopFactory();
            newShopFactory.setName(name);
            newShopFactory.setPinyinAbbr(pinyin4j.toPinYinUppercase(name));
            newShopFactory.setShow(show);
            newShopFactory.setSort(sort);
            newShopFactory.setParentId(parentId);
            newShopFactory.setPoster(poster);
            if (null != parentMerchantNewShopCategory) {
                String parentPath = parentMerchantNewShopCategory.path;
                if (ValidationUtil.isEmpty(parentPath)) parentPath = "/";
                newShopFactory.setPath(parentPath + parentMerchantNewShopCategory.id + "/");
            } else newShopFactory.setPath("/");

            long currentTime = dateUtils.getCurrentTimeBySecond();
            newShopFactory.setCreateTime(currentTime);
            if (!ValidationUtil.isEmpty(imgUrl)) newShopFactory.setImgUrl(imgUrl);
            newShopFactory.save();
            return okJSON200();
        });
    }

    /**
     * @api {POST} /v1/cp/new_shop_factories/:newShopFactoryId/ 04修改新店工厂分类
     * @apiName updateNewShopFactory
     * @apiGroup ADMIN-NEW-SHOP-FACTORY
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
    public CompletionStage<Result> updateNewShopFactory(Http.Request request, long newShopFactoryId) {
        JsonNode requestNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            if (newShopFactoryId < 1) return okCustomJson(CODE40001, "参数错误");
            NewShopFactory newShopCategory = NewShopFactory.find.byId(newShopFactoryId);
            if (null == newShopCategory) return okCustomJson(CODE40002, "该商品分类不存在");
            long parentId = requestNode.findPath("parentId").asLong();
            if (newShopFactoryId == parentId) return okCustomJson(CODE40002, "子分类不能跟父分类一样");
            String name = requestNode.findPath("name").asText();
            int show = requestNode.findPath("show").asInt();
            int sort = requestNode.findPath("sort").asInt();
            if (!ValidationUtil.isEmpty(name)) {
                newShopCategory.setName(name);
                newShopCategory.setPinyinAbbr(pinyin4j.toPinYinUppercase(name));
            }
            if (show > 0) {
                newShopCategory.setShow(show);
                if (show == NewShopCategory.HIDE_CATEGORY) {
                    List<NewShopFactory> list = NewShopFactory.find.query().where().icontains("path", "/" + newShopCategory.id + "/").findList();
                    list.parallelStream().forEach((each) -> {
                        each.setShow(NewShopCategory.HIDE_CATEGORY);
                    });
                    DB.saveAll(list);
                }
            }
            if (sort > 0) newShopCategory.setSort(sort);
            setNewShopFactory(newShopCategory, parentId);
            String imgUrl = requestNode.findPath("imgUrl").asText();
            if (!ValidationUtil.isEmpty(imgUrl)) newShopCategory.setImgUrl(imgUrl);
            String poster = requestNode.findPath("poster").asText();
            if (!ValidationUtil.isEmpty(poster)) newShopCategory.setPoster(poster);
            newShopCategory.save();
            return okJSON200();
        });
    }

    private void setNewShopFactory(NewShopFactory newShopCategory, long parentId) {
        if (parentId > -1) {
            newShopCategory.setParentId(parentId);
            if (parentId > 0) {
                NewShopFactory parentMerchantNewShopCategory = NewShopFactory.find.byId(parentId);
                if (null != parentMerchantNewShopCategory) {
                    String parentPath = parentMerchantNewShopCategory.path;
                    if (ValidationUtil.isEmpty(parentPath)) parentPath = "/";
                    newShopCategory.setPath(parentPath + parentMerchantNewShopCategory.id + "/");
                }
            } else newShopCategory.setPath("/");
        }
    }

    /**
     * @api {POST} /v1/cp/new_shop_factories/ 05删除新店工厂分类
     * @apiName deleteNewShopFactory
     * @apiGroup ADMIN-NEW-SHOP-FACTORY
     * @apiParam {int} newShopFactoryId 工厂分类id
     * @apiParam {String} operation 操作,"del"为删除
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     * @apiSuccess (Error 40002) {int} code 40002 该分类不存在
     * @apiSuccess (Error 40003) {int} code 40003 该分类为父级分类,不能直接删除
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> deleteNewShopFactory(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        String operation = jsonNode.findPath("operation").asText();
        return CompletableFuture.supplyAsync(() -> {
            if (ValidationUtil.isEmpty(operation) || !operation.equals("del")) return okCustomJson(CODE40001, "参数错误");
            long newShopFactoryId = jsonNode.findPath("newShopFactoryId").asInt();
            if (newShopFactoryId < 1) return okCustomJson(CODE40001, "参数错误");
            NewShopFactory newShopFactory = NewShopFactory.find.byId(newShopFactoryId);
            NewShopFactory subFactories = NewShopFactory.find.query().where().eq("parentId", newShopFactory.id).setMaxRows(1).findOne();
            if (null != subFactories) return okCustomJson(CODE40003, "该分类为父级分类,不能直接删除");
            newShopFactory.delete();
            return okJSON200();
        });
    }

    /**
     * @api {POST} /v1/cp/batch_update_new_shop_factory/ 16批量绑定或解绑平台新店分类中的工厂
     * @apiName batchUpdateNewShopFactory
     * @apiGroup ADMIN-NEW-SHOP-FACTORY
     * @apiParam {long} id 分类ID
     * @apiParam {JsonArray} list [{'shopId':11015,'op':'add'},{'shopId':11015,'op':'del'}]
     * @apiSuccess (Success 200){int} code 200
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> batchUpdateNewShopFactory(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            if (null == jsonNode) return okCustomJson(CODE40001, "参数错误");
            long id = jsonNode.findPath("id").asLong();
            NewShopFactory newShopCategory = NewShopFactory.find.byId(id);
            if (null == newShopCategory) return okCustomJson(CODE40002, "该分类不存在");
            ArrayNode list = (ArrayNode) jsonNode.findPath("list");
            List<Shop> shopList = new ArrayList<>();
            list.forEach((node) -> {
                long shopId = node.findPath("shopId").asLong();
                String op = node.findPath("op").asText();
                if (shopId > 0) {
                    Shop shop = Shop.find.byId(shopId);
                    if (null != shop) {
                        Set<Long> set = new HashSet();
                        if (!ValidationUtil.isEmpty(shop.newShopCategoryId)) {
                            ArrayNode categories = (ArrayNode) Json.parse(shop.newShopCategoryId);
                            if (null != categories && categories.size() > 0) {
                                categories.forEach((each) -> {
                                    set.add(each.asLong());
                                });
                            }
                        }
                        if (op.equalsIgnoreCase("add")) set.add(id);
                        else set.remove(id);
                        shop.setNewShopCategoryId(Json.stringify(Json.toJson(set)));
                        shopList.add(shop);
                    }
                }
            });
            if (shopList.size() > 0) {
                DB.saveAll(shopList);
            }
            return okJSON200();
        });
    }


}
