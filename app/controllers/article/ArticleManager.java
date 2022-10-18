package controllers.article;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import constants.BusinessConstant;
import controllers.BaseAdminSecurityController;
import io.ebean.DB;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import models.article.Article;
import models.article.ArticleCategory;
import models.msg.Msg;
import models.user.Member;
import play.Logger;
import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import utils.ValidationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static constants.RedisKeyConstant.ARTICLE_JSON_CACHE;
import static models.article.ArticleCategory.TYPE_NEWS;

/**
 * 文章管理
 */
public class ArticleManager extends BaseAdminSecurityController {
    Logger.ALogger logger = Logger.of(ArticleManager.class);

    /**
     * @api {GET} /v1/cp/articles/?cateId=&cateName=&page= 01查看文章列表
     * @apiName listArticles
     * @apiGroup ADMIN-ARTICLE
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200) {JsonObject} list 列表
     * @apiSuccess (Success 200){long} articleId 文章id
     * @apiSuccess (Success 200){String} title 文章标题
     * @apiSuccess (Success 200){String} author 文章作者
     * @apiSuccess (Success 200){String} source 文章来源
     * @apiSuccess (Success 200){String} publishTime 文章发布时间
     * @apiSuccess (Success 200){String} categoryName 所属分类
     * @apiSuccess (Success 200){String} publishStatusName 发布状态
     */
    public CompletionStage<Result> listArticles(int cateId, String cateName, int page) {
        return CompletableFuture.supplyAsync(() -> {
            int finalPage = page;
            if (finalPage < 1) finalPage = 1;
            ExpressionList<Article> expressionList = Article.find.query().where();
            if (cateId > 0) expressionList.eq("categoryId", cateId);
            else {
                if (!ValidationUtil.isEmpty(cateName)) {
                    ArticleCategory category = ArticleCategory.find.query().where().eq("name", cateName).setMaxRows(1).findOne();
                    if (null == category) return okCustomJson(CODE40001, "分类不存在");
                    expressionList.eq("categoryId", category.getId());
                }
            }
            ObjectNode node = Json.newObject();
            node.put(CODE, CODE200);
            List<Article> list = new ArrayList<>();
            if (page == 0) {
                list = expressionList
                        .orderBy().desc("isTop")
                        .orderBy().desc("sort")
                        .orderBy().desc("publishTime")
                        .findList();
            } else {
                PagedList<Article> pagedList = expressionList
                        .orderBy().desc("isTop")
                        .orderBy().desc("sort")
                        .orderBy().desc("publishTime")
                        .setFirstRow((finalPage - 1) * BusinessConstant.PAGE_SIZE_10)
                        .setMaxRows(BusinessConstant.PAGE_SIZE_10)
                        .findPagedList();
                int pages = pagedList.getTotalPageCount();
                node.put("pages", pages);
                list = pagedList.getList();
            }
            ArrayNode nodes = Json.newArray();
            list.forEach((article -> nodes.add(selectArticleDigest(article))));
            node.set("list", nodes);
            return ok(node);
        });
    }

    public ObjectNode selectArticleDigest(Article each) {
        ObjectNode node = Json.newObject();
        node.put("title", each.getTitle());
        node.put("publishTime", each.getPublishTime());
        node.put("publishDay", each.getPublishDay());
        node.put("id", each.getId());
        node.put("categoryId", each.getCategoryId());
        String cateName = "";
        ArticleCategory articleCategory = ArticleCategory.find.byId(each.getCategoryId());
        if (null != articleCategory) cateName = articleCategory.getName();
        node.put("categoryName", cateName);
        node.put("sort", each.getSort());
        node.put("isTop", each.isTop());
        node.put("isRecommend", each.isRecommend());
        node.put("digest", each.getDigest());
        node.put("headPic", each.getHeadPic());
        node.put("status", each.getStatus());
        node.put("updateTime", each.getUpdateTime());
        node.put("createTime", each.getCreatedTime());
        return node;
    }

    /**
     * @api {GET} /v1/cp/articles/:articleId/ 02文章详情
     * @apiName getArticle
     * @apiGroup ADMIN-ARTICLE
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200){String} title 文章标题
     * @apiSuccess (Success 200){String} author 文章作者
     * @apiSuccess (Success 200){String} source 文章来源
     * @apiSuccess (Success 200){String} publishTime 文章发布时间
     * @apiSuccess (Success 200){String} content 文章内容
     * @apiSuccess (Success 200){String} categoryName 分类名字
     * @apiSuccess (Success 200){int} top 是否置顶 1否 2是
     * @apiSuccess (Success 200){int} recommend 是否推荐 1否2 是
     * @apiSuccess (Success 200){long} startTime 文章有效时间开始
     * @apiSuccess (Success 200){long} endTime 文章有效时间结束
     * @apiSuccess (Success 200){long} updateTime 文章更新时间
     * @apiSuccess (Success 200){long} createdTime 文章创建时间
     * @apiSuccess (Error 40001){int} code 40001 参数错误
     * @apiSuccess (Error 40002){int} code 40002 文章不存在
     */
    public CompletionStage<Result> getArticle(long articleId) {
        return CompletableFuture.supplyAsync(() -> {
            if (articleId < 1) return okCustomJson(CODE40001, "文章ID有误");
            Article article = Article.find.byId(articleId);
            if (null == article) return okCustomJson(CODE40002, "文章不存在");
            ObjectNode node = (ObjectNode) Json.toJson(article);
            node.put(CODE, CODE200);
            return ok(node);
        });
    }

    /**
     * @api {POST} /v1/cp/articles/new/ 03添加文章
     * @apiName addArticle
     * @apiGroup ADMIN-ARTICLE
     * @apiParam {String} title 文章标题
     * @apiParam {String} [author] 文章作者
     * @apiParam {String} [source] 文章来源
     * @apiParam {String} [publishTime] 文章发布时间
     * @apiParam {long} [startTime] 文章有效时间开始
     * @apiParam {long} [endTime] 文章有效时间结束
     * @apiParam {String} content 文章内容
     * @apiParam {String} [digest] 摘要
     * @apiParam {int} categoryId 分类id
     * @apiParam {int} top 是否置顶 1否 2是
     * @apiParam {int} recommend 是否推荐 1否2 是
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> addArticle(Http.Request request) {
        JsonNode requestNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            if (null == requestNode) return okCustomJson(CODE40001, "参数错误");
            Article article = Json.fromJson(requestNode, Article.class);
            if (null == article) return okCustomJson(CODE40001, "参数错误");
            if (ValidationUtil.isEmpty(article.getTitle())
                    || ValidationUtil.isEmpty(article.getContent())
                    || article.getCategoryId() < 1) return okCustomJson(CODE40001, "参数错误");
            long currentTime = dateUtils.getCurrentTimeBySecond();
            if (ValidationUtil.isEmpty(article.getAuthor())) article.setAuthor("本站");
            if (ValidationUtil.isEmpty(article.getSource())) article.setSource("本站");
            if (ValidationUtil.isEmpty(article.getDigest())) article.setDigest(article.getTitle());
            if (article.getPublishTime() < 1) article.setPublishTime(currentTime);
            article.setCreatedTime(currentTime);
            article.setUpdateTime(currentTime);
            article.setStatus(Article.ARTICLE_STATUS_NORMAL);
            article.save();
            businessUtils.deleteArticleCache("", 0);
            return okJSON200();
        });
    }

    /**
     * @api {POST} /v1/cp/articles/:articleId/ 04更新文章
     * @apiName updateArticle
     * @apiGroup ADMIN-ARTICLE
     * @apiParam {String} title 文章标题
     * @apiParam {String} author 文章作者
     * @apiParam {String} source 文章来源
     * @apiParam {String} publishTime 文章发布时间
     * @apiParam {long} startTime 文章有效时间开始
     * @apiParam {long} endTime 文章有效时间结束
     * @apiParam {String} content 文章内容
     * @apiParam {String} digest 摘要
     * @apiParam {int} categoryId 分类id
     * @apiParam {int} top 是否置顶 1否 2是
     * @apiParam {int} recommend 是否推荐 1否2 是
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     * @apiSuccess (Error 40002) {int} code 40002 文章不存在
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> updateArticle(Http.Request request, long id) {
        return CompletableFuture.supplyAsync(() -> {
            JsonNode requestNode = request.body().asJson();
            if (null == requestNode) return okCustomJson(CODE40001, "参数错误");
            logger.info("articleId:" + id);
            logger.info(requestNode.toString());
            Article param = Json.fromJson(requestNode, Article.class);
            if (null == param) return okCustomJson(CODE40001, "参数错误");
            Article resultArticle = Article.find.byId(id);
            if (null == resultArticle) return okCustomJson(CODE40002, "文章不存在");
            if (requestNode.has("title")) resultArticle.setTitle(param.getTitle());
            if (requestNode.has("author")) resultArticle.setAuthor(param.getAuthor());
            if (requestNode.has("source")) resultArticle.setSource(param.getSource());
            if (param.getPublishTime() > 0) resultArticle.setPublishTime(param.getPublishTime());
            if (requestNode.has("content")) resultArticle.setContent(param.getContent());
            if (requestNode.has("digest")) resultArticle.setDigest(param.getDigest());
            if (param.getCategoryId() > 0) resultArticle.setCategoryId(param.getCategoryId());
            if (requestNode.has("top")) resultArticle.setTop(requestNode.findPath("top").asBoolean());
            if (requestNode.has("recommend"))
                resultArticle.setRecommend(requestNode.findPath("recommend").asBoolean());
            if (requestNode.has("status")) resultArticle.setStatus(param.getStatus());
            if (requestNode.has("sort")) resultArticle.setSort(param.getSort());
            if (requestNode.has("headPic")) resultArticle.setHeadPic(param.getHeadPic());
            if (requestNode.has("tags")) resultArticle.setTags(param.getTags());
            if (requestNode.has("productIdList")) {
                logger.info(requestNode.findPath("productIdList").asText());
                resultArticle.setProductIdList(requestNode.findPath("productIdList").asText());
            }
            long currentTime = dateUtils.getCurrentTimeBySecond();
            resultArticle.setUpdateTime(currentTime);
            resultArticle.save();
            String key = ARTICLE_JSON_CACHE + id;
            syncCache.remove(key);
            businessUtils.deleteArticleCache(resultArticle.getCategoryName(), id);
            return okJSON200();
        });
    }

    /**
     * @api {POST} /v1/cp/article/ 05删除文章
     * @apiName delArticle
     * @apiGroup ADMIN-ARTICLE
     * @apiParam {Array} list 文章id数组
     * @apiParam {String} operation 操作,"del"为删除
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     * @apiSuccess (Error 40002) {int} code 40002 该文章不存在
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> delArticle(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            String operation = jsonNode.findPath("operation").asText();
            if (ValidationUtil.isEmpty(operation) || !operation.equals("del")) return okCustomJson(CODE40001, "请求参数错误");
            if (!jsonNode.has("list")) return okCustomJson(CODE40001, "ID列表为空");
            ArrayNode list = (ArrayNode) jsonNode.findPath("list");
            if (list.size() < 1) return okCustomJson(CODE40001, "ID列表为空");
            List<Article> deleteList = new ArrayList<>();
            list.forEach((each) -> {
                long articleId = each.asLong();
                Article article = Article.find.byId(articleId);
                if (null != article) {
                    deleteList.add(article);
                }
            });
            if (deleteList.size() > 0) {
                deleteList.parallelStream().forEach((each) -> {
                    businessUtils.deleteArticleCache(each.getCategoryName(), each.getId());
                });
                DB.deleteAll(deleteList);
            }

            return okJSON200();
        });
    }

    /**
     * @api {GET} /v1/cp/article_categories/?page=1&categoryName=  06文章分类列表
     * @apiName listCategories
     * @apiGroup ADMIN-ARTICLE
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200) {JsonArray} list 文章分类列表
     * @apiSuccess (Success 200) {long} id 文章分类id
     * @apiSuccess (Success 200) {int} sort 排序
     * @apiSuccess (Success 200){String} name 分类名称
     * @apiSuccess (Success 200){String} description 备注
     * @apiSuccess (Success 200){int} status 1显示 2隐藏
     * @apiSuccess (Success 200){long} updateTime 更新时间
     * @apiSuccess (Success 200){long} createdTime 创建时间
     */
    public CompletionStage<Result> listArticleCategories(int page, String categoryName) {
        return CompletableFuture.supplyAsync(() -> {
            ExpressionList<ArticleCategory> expressionList = ArticleCategory.find.query().where();
            if (!ValidationUtil.isEmpty(categoryName)) expressionList.icontains("name", categoryName);
            PagedList<ArticleCategory> pagedList = expressionList.orderBy().asc("id")
                    .setFirstRow((page - 1) * BusinessConstant.PAGE_SIZE_10)
                    .setMaxRows(BusinessConstant.PAGE_SIZE_10).findPagedList();
            int pages = pagedList.getTotalPageCount();
            List<ArticleCategory> categories = pagedList.getList();
            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            result.put("pages", pages);
            result.set("list", Json.toJson(categories));
            return ok(result);
        });
    }


    /**
     * @api {GET} /v1/cp/article_categories/:categoryId/ 07分类详情
     * @apiName getCategory
     * @apiGroup ADMIN-ARTICLE
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200) {long} id 文章分类id
     * @apiSuccess (Success 200) {int} sort 排序
     * @apiSuccess (Success 200){String} name 分类名称
     * @apiSuccess (Success 200){String} description 备注
     * @apiSuccess (Success 200){int} status 1显示 2隐藏
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     * @apiSuccess (Error 40002) {int} code 40002 分类不存在
     */
    public CompletionStage<Result> getArticleCategory(int categoryId) {
        return CompletableFuture.supplyAsync(() -> {
            if (categoryId < 1) return okCustomJson(CODE40001, "参数错误");
            ArticleCategory category = ArticleCategory.find.byId(categoryId);
            if (null == category) return okCustomJson(CODE40002, "该分类不存在");
            ObjectNode node = (ObjectNode) Json.toJson(category);
            node.put(CODE, CODE200);
            return ok(node);
        });
    }

    /**
     * @api {POST} /v1/cp/article_categories/new/ 08添加文章分类
     * @apiName addCategory
     * @apiGroup ADMIN-ARTICLE
     * @apiParam {int} sort 排序
     * @apiParam {String} name 分类名称
     * @apiParam {String} description 备注
     * @apiParam {int} status 1显示 2隐藏
     * @apiParam {int} categoryType 1为资讯类，2为帮助类
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> addArticleCategory(Http.Request request) {
        JsonNode requestNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            if (null == requestNode) return okCustomJson(CODE40001, "参数错误");
            ArticleCategory param = Json.fromJson(requestNode, ArticleCategory.class);
            if (null == param) return okCustomJson(CODE40001, "参数错误");
            if (ValidationUtil.isEmpty(param.getName()))
                return okCustomJson(CODE40001, "请输入分类名字");
            ArticleCategory exist = ArticleCategory.find.query().where().eq("name", param.getName()).setMaxRows(1).findOne();
            if (null != exist) return okCustomJson(CODE40002, "该分类已存在");
            if (param.getCategoryType() < 1) param.setCategoryType(TYPE_NEWS);
            if (param.getStatus() < 1) param.setStatus(ArticleCategory.SHOW);
            long currentTime = dateUtils.getCurrentTimeBySecond();
            param.setUpdateTime(currentTime);
            param.setCreatedTime(currentTime);
            param.save();
            syncCache.set(cacheUtils.getArticleCategoryKey(param.getName()), param);
            businessUtils.deleteArticleCache("", 0);
            return okJSON200();
        });
    }

    /**
     * @api {POST} /v1/cp/article_categories/:categoryId/ 09更新文章分类
     * @apiName updateCategory
     * @apiGroup ADMIN-ARTICLE
     * @apiParam {int} sort 排序
     * @apiParam {String} name 分类名称
     * @apiParam {String} description 备注
     * @apiParam {int} status 1显示 2隐藏
     * @apiParam {int} categoryType 1为资讯类，2为帮助类
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     * @apiSuccess (Error 40002) {int} code 40002 分类不存在
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> updateArticleCategory(Http.Request request, int categoryId) {
        JsonNode requestNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            if (null == requestNode || categoryId < 1) return okCustomJson(CODE40001, "参数错误");
            ArticleCategory param = Json.fromJson(requestNode, ArticleCategory.class);
            if (null == param) return okCustomJson(CODE40001, "参数错误");
            ArticleCategory resultCategory = ArticleCategory.find.byId(categoryId);
            if (null == resultCategory) return okCustomJson(CODE40002, "该分类不存在");
            businessUtils.deleteArticleCache(resultCategory.getName(), 0);
            if (!ValidationUtil.isEmpty(param.getName())) {
                resultCategory.setName(param.getName());
                if (!param.getName().equalsIgnoreCase(resultCategory.getName())) {
                    List<Article> articleList = Article.find.query().where().eq("categoryId", resultCategory.getId()).findList();
                    if (articleList.size() > 0) {
                        articleList.parallelStream().forEach((each) -> {
                            each.setCategoryName(resultCategory.getName());
                        });
                        DB.saveAll(articleList);
                    }
                }
            }
            if (!ValidationUtil.isEmpty(param.getNote())) resultCategory.setNote(param.getNote());
            if (requestNode.has("headPic")) resultCategory.setHeadPic(param.getHeadPic());
            if (requestNode.has("displayMode")) resultCategory.setDisplayMode(param.getDisplayMode());
            if (param.getSort() > 0) resultCategory.setSort(param.getSort());
            if (param.getStatus() > 0) resultCategory.setStatus(param.getStatus());
            if (param.getCategoryType() > 0) resultCategory.setCategoryType(param.getCategoryType());
            if (!ValidationUtil.isEmpty(param.getIcon())) resultCategory.setIcon(param.getIcon());
            resultCategory.setUpdateTime(dateUtils.getCurrentTimeBySecond());
            resultCategory.save();

            syncCache.set(cacheUtils.getArticleCategoryKey(resultCategory.getName()), resultCategory);
            businessUtils.deleteArticleCache(param.getName(), 0);
            return okJSON200();
        });
    }

    /**
     * @api {POST} /v1/cp/article_categories/ 10删除文章分类
     * @apiName delCategory
     * @apiGroup ADMIN-ARTICLE
     * @apiParam {int} categoryId 分类id
     * @apiParam {String} operation 操作,"del"为删除
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     * @apiSuccess (Error 40002) {int} code 40002 该分组不存在
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> delArticleCategory(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            String operation = jsonNode.findPath("operation").asText();
            if (ValidationUtil.isEmpty(operation) || !operation.equals("del")) return okCustomJson(CODE40001, "请求参数错误");
            int categoryId = jsonNode.findPath("categoryId").asInt();
            if (categoryId < 1) return okCustomJson(CODE40001, "分类ID有误");
            ArticleCategory category = ArticleCategory.find.byId(categoryId);
            if (null == category) return okCustomJson(CODE40002, "该分类不存在");
            syncCache.remove(cacheUtils.getArticleCategoryKey(category.getName()));
            category.delete();
            businessUtils.deleteArticleCache(category.getName(), 0);
            return okJSON200();
        });
    }

    /**
     * @api {GET} /v1/cp/article_all_categories/  11所有文章分类列表
     * @apiName listCategories
     * @apiGroup ADMIN-ARTICLE
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200) {JsonArray} list 文章分类列表
     * @apiSuccess (Success 200) {long} id 文章分类id
     * @apiSuccess (Success 200) {int} sort 排序
     * @apiSuccess (Success 200){String} name 分类名称
     * @apiSuccess (Success 200){String} description 备注
     * @apiSuccess (Success 200){int} status 1显示 2隐藏
     * @apiSuccess (Success 200){long} updateTime 更新时间
     * @apiSuccess (Success 200){long} createdTime 创建时间
     */
    public CompletionStage<Result> listAllCategories() {
        return CompletableFuture.supplyAsync(() -> {
            List<ArticleCategory> list = ArticleCategory.find.all();
            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            result.set("list", Json.toJson(list));
            return ok(result);
        });
    }

    /**
     * @api {POST} /v1/cp/push_msg/ 12发布系统公告消息
     * @apiName pushMsg
     * @apiGroup ADMIN-ARTICLE
     * @apiParam {long} articleId articleId
     * @apiSuccess (Success 200) {int} code 200 请求成功
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> pushMsg(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            long articleId = jsonNode.findPath("articleId").asLong();
            if (articleId < 1) return okCustomJson(CODE40001, "文章ID有误");
            Article article = Article.find.byId(articleId);
            if (null == article) return okCustomJson(CODE40001, "文章ID有误");
            List<Member> members = Member.find.all();
            long currentTime = dateUtils.getCurrentTimeBySecond();
            List<Msg> updateList = new ArrayList<>();
            members.parallelStream().forEach((each) -> {
                Msg msg = new Msg();
                msg.setUid(each.id);
                msg.setCategoryId(article.getCategoryId());
                msg.setTitle(article.getTitle());
                String desc = article.getDigest();
                if (ValidationUtil.isEmpty(desc)) desc = msg.title;
                msg.setContent(desc);
                msg.setLinkUrl(article.getId() + "");
                msg.setMsgType(Msg.MSG_TYPE_SYSTEM);
                msg.setStatus(Msg.STATUS_NOT_READ);
                msg.setArticleId(articleId);
                msg.setItemId(0);
                msg.setChangeAmount(0);
                msg.setCreateTime(currentTime);
                updateList.add(msg);
            });
            if (updateList.size() > 0) DB.saveAll(updateList);
            return okJSON200();
        });
    }


}
