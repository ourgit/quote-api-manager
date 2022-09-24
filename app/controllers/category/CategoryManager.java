package controllers.category;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.BaseAdminSecurityController;
import io.ebean.DB;
import io.ebean.ExpressionList;
import models.post.Category;
import play.Logger;
import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import utils.Pinyin4j;
import utils.ValidationUtil;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static models.post.Category.CATE_TYPE_POST;


public class CategoryManager extends BaseAdminSecurityController {
    @Inject
    Pinyin4j pinyin4j;

    Logger.ALogger logger = Logger.of(CategoryManager.class);

    /**
     * @api {GET} /v1/cp/categories/?page=&filter=&parentId= 01分类列表
     * @apiName listCategories
     * @apiGroup ADMIN-CATEGORY
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
    public CompletionStage<Result> listCategories(final String filter, long parentId, int cateType) {
        return CompletableFuture.supplyAsync(() -> {
            ExpressionList<Category> expressionList = Category.find.query().where();
            if (!ValidationUtil.isEmpty(filter)) expressionList.icontains("name", filter);
            if (parentId >= 0) expressionList.eq("parentId", parentId);
            if (cateType > 0) expressionList.eq("cateType", cateType);
            List<Category> list = expressionList.orderBy().desc("sort").findList();
            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            List<Category> resultList = businessUtils.convertListToTreeNode(list);
            result.set("list", Json.toJson(resultList));
            return ok(result);
        });
    }

    /**
     * @api {GET} /v1/cp/categories/:categoryId/ 02分类详情
     * @apiName getCategory
     * @apiGroup ADMIN-CATEGORY
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200){long} id 分类id
     * @apiSuccess (Success 200){String} name 名称
     * @apiSuccess (Success 200){int} show 1显示2不显示
     * @apiSuccess (Success 200){long} parentId 父类id
     * @apiSuccess (Success 200){int} sort 排序顺序
     * @apiSuccess (Success 200){JsonArray} children 子列表
     * @apiSuccess (Success 200){string} updateTime 更新时间
     */
    public CompletionStage<Result> getCategory(long categoryId) {
        return CompletableFuture.supplyAsync(() -> {
            if (categoryId < 1) return okCustomJson(CODE40001, "参数错误");
            Category category = Category.find.byId(categoryId);
            if (null == category) return okCustomJson(CODE40002, "该商品分类不存在");
            List<Category> children = Category.find.query().where().eq("parentId", categoryId).findList();
            category.children = children;
            ObjectNode result = (ObjectNode) Json.toJson(category);
            result.put(CODE, CODE200);
            return ok(result);
        });
    }

    /**
     * @api {POST} /v1/cp/categories/new/ 03添加分类
     * @apiName addCategory
     * @apiGroup ADMIN-CATEGORY
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
    public CompletionStage<Result> addCategory(Http.Request request) {
        JsonNode requestNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            String name = requestNode.findPath("name").asText();
            int show = requestNode.findPath("show").asInt();
            long parentId = requestNode.findPath("parentId").asLong();
            int sort = requestNode.findPath("sort").asInt();
            int cateType = requestNode.findPath("cateType").asInt();
            if (cateType < 1) cateType = CATE_TYPE_POST;
            String imgUrl = requestNode.findPath("imgUrl").asText();
            String poster = requestNode.findPath("poster").asText();
            if (ValidationUtil.isEmpty(name)) return okCustomJson(CODE40001, "参数错误");
            Category parentMerchantCategory = null;
            if (parentId > 0) {
                parentMerchantCategory = Category.find.byId(parentId);
                if (null == parentMerchantCategory) return okCustomJson(CODE40001, "父类ID不存在");
            }
            if (show < 1) show = 1;
            Category category = new Category();
            category.setName(name);
            category.setPinyinAbbr(pinyin4j.toPinYinUppercase(name));
            category.setShow(show);
            category.setSort(sort);
            category.setCateType(cateType);
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
            category.setPathName(getPathName(category.path));
            category.save();
            updateCategoryCache();
            return okJSON200();
        });
    }

    private String getPathName(String path) {
        List<Category> list = Category.find.all();
        Map<Long, String> map = new HashMap<>();
        list.parallelStream().forEach((each) -> {
            map.put(each.id, each.name);
        });
        String[] pathList = path.split("/");
        StringBuilder sb = new StringBuilder();
        sb.append("/");
        Arrays.stream(pathList).forEach((eachPath) -> {
            if (!ValidationUtil.isEmpty(eachPath)) {
                if (eachPath.equalsIgnoreCase("/")) sb.append(eachPath);
                else {
                    Long id = Long.parseLong(eachPath);
                    String result = map.get(id);
                    sb.append(result).append("/");
                }
            }
        });
        return sb.toString();
    }

    /**
     * @api {POST} /v1/cp/categories/:categoryId/ 04修改分类
     * @apiName updateCategory
     * @apiGroup ADMIN-CATEGORY
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
    public CompletionStage<Result> updateCategory(Http.Request request, long categoryId) {
        JsonNode requestNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            if (categoryId < 1) return okCustomJson(CODE40001, "参数错误");
            Category category = Category.find.byId(categoryId);
            if (null == category) return okCustomJson(CODE40002, "该商品分类不存在");
            long parentId = requestNode.findPath("parentId").asLong();
            if (categoryId == parentId) return okCustomJson(CODE40002, "子分类不能跟父分类一样");
            String name = requestNode.findPath("name").asText();
            int cateType = requestNode.findPath("cateType").asInt();

            int show = requestNode.findPath("show").asInt();
            int sort = requestNode.findPath("sort").asInt();
            if (!ValidationUtil.isEmpty(name)) {
                category.setName(name);
                category.setPinyinAbbr(pinyin4j.toPinYinUppercase(name));
            }
            if (show > 0) {
                category.setShow(show);
                if (show == Category.HIDE_CATEGORY) {
                    List<Category> list = Category.find.query().where().icontains("path", "/" + category.id + "/").findList();
                    list.parallelStream().forEach((each) -> {
                        each.setShow(Category.HIDE_CATEGORY);
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
            if (cateType > 0) category.setCateType(cateType);
            category.setPathName(getPathName(category.path));
            category.save();
            updateCategoryCache();
            return okJSON200();
        });
    }

    private void setCategory(Category category, long parentId) {
        if (parentId > -1) {
            category.setParentId(parentId);
            if (parentId > 0) {
                Category parentMerchantCategory = Category.find.byId(parentId);
                if (null != parentMerchantCategory) {
                    String parentPath = parentMerchantCategory.path;
                    if (ValidationUtil.isEmpty(parentPath)) parentPath = "/";
                    category.setPath(parentPath + parentMerchantCategory.id + "/");
                }
            } else category.setPath("/");
        }
    }

    /**
     * @api {POST} /v1/cp/categories/ 05删除分类
     * @apiName deleteCategory
     * @apiGroup ADMIN-CATEGORY
     * @apiParam {int} categoryId 商品分类id
     * @apiParam {String} operation 操作,"del"为删除
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     * @apiSuccess (Error 40002) {int} code 40002 该分类不存在
     * @apiSuccess (Error 40003) {int} code 40003 该分类为父级分类,不能直接删除
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> deleteCategory(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        String operation = jsonNode.findPath("operation").asText();
        return CompletableFuture.supplyAsync(() -> {
            if (ValidationUtil.isEmpty(operation) || !operation.equals("del")) return okCustomJson(CODE40001, "参数错误");
            long categoryId = jsonNode.findPath("categoryId").asInt();
            if (categoryId < 1) return okCustomJson(CODE40001, "参数错误");
            Category category = Category.find.byId(categoryId);
            Category subCategories = Category.find.query().where().eq("parentId", category.id).setMaxRows(1).findOne();
            if (null != subCategories) return okCustomJson(CODE40003, "该分类为父级分类,不能直接删除");
            category.delete();
            updateCategoryCache();
            return okJSON200();
        });
    }

    private void updateCategoryCache() {
        cacheUtils.updateMerchantCategoryCache();
    }

}
