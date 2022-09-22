package controllers.ad;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import constants.BusinessConstant;
import controllers.BaseAdminSecurityController;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import models.ad.Ad;
import models.admin.AdminMember;
import models.article.Article;
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
     * @apiParam {String} title title
     * @apiParam {String} content content
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200) {JsonArray} list 菜单列表
     * @apiSuccess (Success 200) {long} id id
     * @apiSuccess (Success 200){String} title 标题，这个也可能是商家的名字，放在第一行
     * @apiSuccess (Success 200){String} digest 摘要,需要允许换行
     * @apiSuccess (Success 200){String} content 内容
     * @apiSuccess (Success 200){String} img1 图片1，这里也可以换成一个视频
     * @apiSuccess (Success 200){String} img2 图片2
     * @apiSuccess (Success 200){String} img3 图片3
     * @apiSuccess (Success 200){String} img4 图片4
     * @apiSuccess (Success 200){String} img5 图片5
     * @apiSuccess (Success 200){String} img6 图片6
     * @apiSuccess (Success 200){String} img7 图片7
     * @apiSuccess (Success 200){String} img8 图片8
     * @apiSuccess (Success 200){String} img9 图片9
     * @apiSuccess (Success 200){String} linkUrl 链接地址
     * @apiSuccess (Success 200){long} budget 预算奖励
     * @apiSuccess (Success 200){long} views 阅读数
     * @apiSuccess (Success 200){long} sort 排序
     * @apiSuccess (Success 200){long} articleId 广告文章ID
     * @apiSuccess (Success 200){int} status 状态1正常  -1下架
     * @apiSuccess (Success 200){long} beginTime beginTime 开始时间
     * @apiSuccess (Success 200){long} endTime endTime 结束时间
     * @apiSuccess (Success 200){long} updateTime updateTime 更新时间
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> listAd(Http.Request request) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            if (null == admin) return unauth403();
            JsonNode requestNode = request.body().asJson();
            String title = requestNode.findPath("title").asText();
            String content = requestNode.findPath("content").asText();
            int page = requestNode.findPath("page").asInt();
            int status = requestNode.findPath("status").asInt();
            ExpressionList<Ad> expressionList = Ad.find.query().where();
            if (status != 0) expressionList.eq("status", status);
            if (!ValidationUtil.isEmpty(title)) expressionList.icontains("title", title);
            if (!ValidationUtil.isEmpty(content)) expressionList.icontains("content", content);
            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            PagedList<Ad> pagedList = expressionList
                    .orderBy().desc("sort")
                    .setFirstRow((page - 1) * BusinessConstant.PAGE_SIZE_20)
                    .setMaxRows(BusinessConstant.PAGE_SIZE_20)
                    .findPagedList();
            int pages = pagedList.getTotalPageCount();
            List<Ad> list = pagedList.getList();
            result.set("list", Json.toJson(list));
            result.put("pages", pages);
            return ok(result);
        });
    }

    /**
     * @api {POST} /v1/cp/ad/new/ 02添加广告
     * @apiName addAd
     * @apiGroup ADMIN-AD
     * @apiParam {String} title 标题，这个也可能是商家的名字，放在第一行
     * @apiParam {String} digest 摘要,需要允许换行
     * @apiParam {String} content 内容
     * @apiParam {String} img1 图片1，这里也可以换成一个视频
     * @apiParam {String} img2 图片2
     * @apiParam {String} img3 图片3
     * @apiParam {String} img4 图片4
     * @apiParam {String} img5 图片5
     * @apiParam {String} img6 图片6
     * @apiParam {String} img7 图片7
     * @apiParam {String} img8 图片8
     * @apiParam {String} img9 图片9
     * @apiParam {String} linkUrl 链接地址
     * @apiParam {long} budget 预算奖励
     * @apiParam {long} articleId 广告文章ID
     * @apiParam {long} views 阅读数
     * @apiParam {long} sort 排序
     * @apiParam {int} status 状态1正常  -1下架
     * @apiParam {long} beginTime beginTime 开始时间
     * @apiParam {long} endTime endTime 结束时间
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     * @apiSuccess (Error 40002) {int} code 40002 该广告已存在
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
            if (ValidationUtil.isEmpty(param.title)) return okCustomJson(CODE40001, "标题不能为空");
            if (ValidationUtil.isEmpty(param.digest)) return okCustomJson(CODE40001, "摘要不能为空");
            if (ValidationUtil.isEmpty(param.img1)) return okCustomJson(CODE40001, "至少一张图片或者视频");
            if (param.articleId < 1) return okCustomJson(CODE40001, "广告详情文章不能为空");
            Article article = Article.find.byId(param.articleId);
            if (null == article) return okCustomJson(CODE40001, "广告详情文章不能为空");
            long currentTime = dateUtils.getCurrentTimeBySecond();
            if (param.status == 0) param.setStatus(Ad.STATUS_NORMAL);
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
            if (requestNode.has("avatar")) ad.setAvatar(param.avatar);
            if (requestNode.has("title")) ad.setTitle(param.title);
            if (requestNode.has("digest")) ad.setDigest(param.digest);
            if (requestNode.has("content")) ad.setContent(param.content);
            if (requestNode.has("img1")) ad.setImg1(param.img1);
            if (requestNode.has("img2")) ad.setImg2(param.img2);
            if (requestNode.has("img3")) ad.setImg3(param.img3);
            if (requestNode.has("img4")) ad.setImg4(param.img4);
            if (requestNode.has("img5")) ad.setImg5(param.img5);
            if (requestNode.has("img6")) ad.setImg6(param.img6);
            if (requestNode.has("img7")) ad.setImg7(param.img7);
            if (requestNode.has("img8")) ad.setImg8(param.img8);
            if (requestNode.has("img9")) ad.setImg9(param.img9);
            if (requestNode.has("linkUrl")) ad.setLinkUrl(param.linkUrl);
            if (requestNode.has("budget")) ad.setBudget(param.budget);
            if (requestNode.has("status")) ad.setStatus(param.status);
            if (requestNode.has("sort")) ad.setSort(param.sort);
            if (requestNode.has("beginTime")) ad.setBeginTime(param.beginTime);
            if (requestNode.has("endTime")) ad.setEndTime(param.endTime);
            if (requestNode.has("articleId")) ad.setArticleId(param.articleId);
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

}
