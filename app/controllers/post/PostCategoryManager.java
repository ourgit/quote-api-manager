package controllers.post;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.BaseAdminSecurityController;
import io.ebean.DB;
import io.ebean.ExpressionList;
import models.post.PostCategory;
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

import static models.post.PostCategory.CATE_TYPE_POST;


public class PostCategoryManager extends BaseAdminSecurityController {
    @Inject
    Pinyin4j pinyin4j;

    Logger.ALogger logger = Logger.of(PostCategoryManager.class);

    /**
     * @api {GET} /v1/cp/post_categories/?page=&filter=&parentId= 01贴子分类列表
     * @apiName listPostCategories
     * @apiGroup ADMIN-POST-CATEGORY
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
    public CompletionStage<Result> listPostCategories(final String filter, long parentId, int cateType) {
        return CompletableFuture.supplyAsync(() -> {
            ExpressionList<PostCategory> expressionList = PostCategory.find.query().where();
            if (!ValidationUtil.isEmpty(filter)) expressionList.icontains("name", filter);
            if (parentId >= 0) expressionList.eq("parentId", parentId);
            if (cateType > 0) expressionList.eq("cateType", cateType);
            List<PostCategory> list = expressionList.orderBy().desc("sort").findList();
            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            List<PostCategory> resultList = businessUtils.convertPostCategoryList(list);
            result.set("list", Json.toJson(resultList));
            return ok(result);
        });
    }

    /**
     * @api {GET} /v1/cp/post_categories/:categoryId/ 02贴子分类详情
     * @apiName getPostCategory
     * @apiGroup ADMIN-POST-CATEGORY
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200){long} id 分类id
     * @apiSuccess (Success 200){String} name 名称
     * @apiSuccess (Success 200){int} show 1显示2不显示
     * @apiSuccess (Success 200){long} parentId 父类id
     * @apiSuccess (Success 200){int} sort 排序顺序
     * @apiSuccess (Success 200){JsonArray} children 子列表
     * @apiSuccess (Success 200){string} updateTime 更新时间
     */
    public CompletionStage<Result> getPostCategory(long categoryId) {
        return CompletableFuture.supplyAsync(() -> {
            if (categoryId < 1) return okCustomJson(CODE40001, "参数错误");
            PostCategory postCategory = PostCategory.find.byId(categoryId);
            if (null == postCategory) return okCustomJson(CODE40002, "该商品分类不存在");
            List<PostCategory> children = PostCategory.find.query().where().eq("parentId", categoryId).findList();
            postCategory.children = children;
            ObjectNode result = (ObjectNode) Json.toJson(postCategory);
            result.put(CODE, CODE200);
            return ok(result);
        });
    }

    /**
     * @api {POST} /v1/cp/post_categories/new/ 03添加贴子分类
     * @apiName addPostCategory
     * @apiGroup ADMIN-POST-CATEGORY
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
    public CompletionStage<Result> addPostCategory(Http.Request request) {
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
            String seoKeyword = requestNode.findPath("seoKeyword").asText();
            String seoDescription = requestNode.findPath("seoDescription").asText();
            if (ValidationUtil.isEmpty(name)) return okCustomJson(CODE40001, "参数错误");
            PostCategory parentMerchantPostCategory = null;
            if (parentId > 0) {
                parentMerchantPostCategory = PostCategory.find.byId(parentId);
                if (null == parentMerchantPostCategory) return okCustomJson(CODE40001, "父类ID不存在");
            }
            if (show < 1) show = 1;
            PostCategory postCategory = new PostCategory();
            postCategory.setName(name);
            postCategory.setPinyinAbbr(pinyin4j.toPinYinUppercase(name));
            postCategory.setShow(show);
            postCategory.setSort(sort);
            postCategory.setCateType(cateType);
            postCategory.setParentId(parentId);
            postCategory.setSeoKeyword(seoKeyword);
            postCategory.setSeoDescription(seoDescription);
            postCategory.setPoster(poster);
            if (null != parentMerchantPostCategory) {
                String parentPath = parentMerchantPostCategory.path;
                if (ValidationUtil.isEmpty(parentPath)) parentPath = "/";
                postCategory.setPath(parentPath + parentMerchantPostCategory.id + "/");
            } else postCategory.setPath("/");

            long currentTime = dateUtils.getCurrentTimeBySecond();
            postCategory.setCreateTime(currentTime);
            if (!ValidationUtil.isEmpty(imgUrl)) postCategory.setImgUrl(imgUrl);
            postCategory.setPathName(getPathName(postCategory.path));
            postCategory.save();
            updateCategoryCache();
            return okJSON200();
        });
    }

    private String getPathName(String path) {
        List<PostCategory> list = PostCategory.find.all();
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
     * @api {POST} /v1/cp/post_categories/:categoryId/ 04修改贴子分类
     * @apiName updatePostCategory
     * @apiGroup ADMIN-POST-CATEGORY
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
    public CompletionStage<Result> updatePostCategory(Http.Request request, long categoryId) {
        JsonNode requestNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            if (categoryId < 1) return okCustomJson(CODE40001, "参数错误");
            PostCategory postCategory = PostCategory.find.byId(categoryId);
            if (null == postCategory) return okCustomJson(CODE40002, "该商品分类不存在");
            long parentId = requestNode.findPath("parentId").asLong();
            if (categoryId == parentId) return okCustomJson(CODE40002, "子分类不能跟父分类一样");
            String name = requestNode.findPath("name").asText();
            int cateType = requestNode.findPath("cateType").asInt();

            int show = requestNode.findPath("show").asInt();
            int sort = requestNode.findPath("sort").asInt();
            if (!ValidationUtil.isEmpty(name)) {
                postCategory.setName(name);
                postCategory.setPinyinAbbr(pinyin4j.toPinYinUppercase(name));
            }
            if (show > 0) {
                postCategory.setShow(show);
                if (show == PostCategory.HIDE_CATEGORY) {
                    List<PostCategory> list = PostCategory.find.query().where().icontains("path", "/" + postCategory.id + "/").findList();
                    list.parallelStream().forEach((each) -> {
                        each.setShow(PostCategory.HIDE_CATEGORY);
                    });
                    DB.saveAll(list);
                }
            }
            if (sort > 0) postCategory.setSort(sort);
            setCategory(postCategory, parentId);
            String imgUrl = requestNode.findPath("imgUrl").asText();
            if (!ValidationUtil.isEmpty(imgUrl)) postCategory.setImgUrl(imgUrl);
            String poster = requestNode.findPath("poster").asText();
            String seoKeyword = requestNode.findPath("seoKeyword").asText();
            String seoDescription = requestNode.findPath("seoDescription").asText();
            if (!ValidationUtil.isEmpty(poster)) postCategory.setPoster(poster);
            if (!ValidationUtil.isEmpty(seoKeyword)) postCategory.setSeoKeyword(seoKeyword);
            if (!ValidationUtil.isEmpty(seoDescription)) postCategory.setSeoDescription(seoDescription);
            if (cateType > 0) postCategory.setCateType(cateType);
            postCategory.setPathName(getPathName(postCategory.path));
            postCategory.save();
            updateCategoryCache();
            return okJSON200();
        });
    }

    private void setCategory(PostCategory postCategory, long parentId) {
        if (parentId > -1) {
            postCategory.setParentId(parentId);
            if (parentId > 0) {
                PostCategory parentMerchantPostCategory = PostCategory.find.byId(parentId);
                if (null != parentMerchantPostCategory) {
                    String parentPath = parentMerchantPostCategory.path;
                    if (ValidationUtil.isEmpty(parentPath)) parentPath = "/";
                    postCategory.setPath(parentPath + parentMerchantPostCategory.id + "/");
                }
            } else postCategory.setPath("/");
        }
    }

    /**
     * @api {POST} /v1/cp/post_categories/ 05删除贴子分类
     * @apiName deletePostCategory
     * @apiGroup ADMIN-POST-CATEGORY
     * @apiParam {int} categoryId 商品分类id
     * @apiParam {String} operation 操作,"del"为删除
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     * @apiSuccess (Error 40002) {int} code 40002 该分类不存在
     * @apiSuccess (Error 40003) {int} code 40003 该分类为父级分类,不能直接删除
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> deletePostCategory(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        String operation = jsonNode.findPath("operation").asText();
        return CompletableFuture.supplyAsync(() -> {
            if (ValidationUtil.isEmpty(operation) || !operation.equals("del")) return okCustomJson(CODE40001, "参数错误");
            long categoryId = jsonNode.findPath("categoryId").asInt();
            if (categoryId < 1) return okCustomJson(CODE40001, "参数错误");
            PostCategory postCategory = PostCategory.find.byId(categoryId);
            PostCategory subCategories = PostCategory.find.query().where().eq("parentId", postCategory.id).setMaxRows(1).findOne();
            if (null != subCategories) return okCustomJson(CODE40003, "该分类为父级分类,不能直接删除");
            postCategory.delete();
            updateCategoryCache();
            return okJSON200();
        });
    }

    private void updateCategoryCache() {
        cacheUtils.updateMerchantCategoryCache();
    }

}
