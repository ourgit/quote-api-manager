package controllers.post;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.BaseController;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import models.post.Post;
import models.post.Reply;
import play.Logger;
import play.i18n.Messages;
import play.i18n.MessagesApi;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static constants.BusinessConstant.PAGE_SIZE_10;


public class PostManager extends BaseController {

    Logger.ALogger logger = Logger.of(PostManager.class);
    @Inject
    MessagesApi messagesApi;

    /**
     * @api {GET} /v1/cp/post_list/?categoryId= 01贴子列表
     * @apiName listPosts
     * @apiGroup Admin-Post
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200) {JsonObject} list 列表
     * @apiSuccess (Success 200){String} title 标题
     * @apiSuccess (Success 200){String} userName 作者
     * @apiSuccess (Success 200){String} avatar 头像
     * @apiSuccess (Success 200){String} content 内容
     * @apiSuccess (Success 200){long} categoryId 分类ID
     * @apiSuccess (Success 200){String} categoryName 分类名字
     * @apiSuccess (Success 200){long} commentNumber 跟贴数
     * @apiSuccess (Success 200){long} replies 回复数
     * @apiSuccess (Success 200){long} participants 参与人数
     * @apiSuccess (Success 200){long} likes 点赞数
     * @apiSuccess (Success 200){String} updateTime 更新时间
     * @apiSuccess (Success 200){String} createTime 创建时间
     */
    public CompletionStage<Result> listPosts(Http.Request request, int categoryId, int page) {
        return CompletableFuture.supplyAsync(() -> {
            ExpressionList<Post> expressionList = Post.find.query().where()
                    .eq("status", Post.STATUS_NORMAL);
            if (categoryId > 0) expressionList.eq("categoryId", categoryId);
            PagedList<Post> pagedList = expressionList
                    .orderBy().desc("id")
                    .setFirstRow((page - 1) * PAGE_SIZE_10)
                    .setMaxRows(PAGE_SIZE_10)
                    .findPagedList();
            ObjectNode node = Json.newObject();
            node.put(CODE, CODE200);
            node.put("hasNext", pagedList.hasNext());
            node.set("list", Json.toJson(pagedList.getList()));
            return ok(node);
        });
    }


    /**
     * @api {GET} /v1/cp/posts/:id/ 02帖子详情
     * @apiName getPost
     * @apiGroup Admin-Post
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200) {Array} replyList 跟贴内容
     * @apiSuccess (Success 200){long} id id
     * @apiSuccess (Success 200){String} title 标题
     * @apiSuccess (Success 200){String} userName 作者
     * @apiSuccess (Success 200){String} avatar 头像
     * @apiSuccess (Success 200){String} content 内容
     * @apiSuccess (Success 200){long} categoryId 分类ID
     * @apiSuccess (Success 200){String} categoryName 分类名字
     * @apiSuccess (Success 200){long} commentNumber 跟贴数
     * @apiSuccess (Success 200){long} replies 回复数
     * @apiSuccess (Success 200){int} status 1正常 -1隐藏
     * @apiSuccess (Success 200){long} participants 参与人数
     * @apiSuccess (Success 200){long} likes 点赞数
     * @apiSuccess (Success 200){String} updateTime 更新时间
     * @apiSuccess (Success 200){String} createTime 创建时间
     */
    public CompletionStage<Result> getPost(Http.Request request, long id) {
        return CompletableFuture.supplyAsync(() -> {
            Post post = Post.find.byId(id);
            List<Reply> replyList = Reply.find.query().where().eq("postId", id)
                    .orderBy().asc("id")
                    .findList();
            ObjectNode node = (ObjectNode) Json.toJson(post);
            node.put(CODE, CODE200);
            node.set("replyList", Json.toJson(replyList));
            return ok(node);
        });
    }


    /**
     * @api {POST} /v1/cp/post/ 03删除贴子
     * @apiName deletePost
     * @apiGroup Admin-Post
     * @apiParam {long} id id
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> deletePost(Http.Request request) {
        return CompletableFuture.supplyAsync(() -> {
            JsonNode jsonNode = request.body().asJson();
            Messages messages = this.messagesApi.preferred(request);
            String baseArgumentError = messages.at("base.argument.error");
            if (null == jsonNode) return okCustomJson(CODE40001, baseArgumentError);
            long id = jsonNode.findPath("id").asLong();
            Post post = Post.find.byId(id);
            if (null == post) return okCustomJson(CODE40001, "该贴子不存在");
            post.setStatus(Post.STATUS_DELETE);
            post.setUpdateTime(dateUtils.getCurrentTimeBySecond());
            post.save();
            return okJSON200();
        });
    }

    /**
     * @api {POST} /v1/cp/post_top/ 04置顶/取消置顶贴子
     * @apiName placePostTop
     * @apiGroup Admin-Post
     * @apiParam {long} id id
     * @apiParam {int} placeTop true置顶 false取消置顶
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> placePostTop(Http.Request request) {
        return CompletableFuture.supplyAsync(() -> {
            JsonNode jsonNode = request.body().asJson();
            Messages messages = this.messagesApi.preferred(request);
            String baseArgumentError = messages.at("base.argument.error");
            if (null == jsonNode) return okCustomJson(CODE40001, baseArgumentError);
            long id = jsonNode.findPath("id").asLong();
            boolean placeTop = jsonNode.findPath("placeTop").asBoolean();
            Post post = Post.find.byId(id);
            if (null == post) return okCustomJson(CODE40001, "该贴子不存在");
            post.setPlaceTop(placeTop);
            post.setUpdateTime(dateUtils.getCurrentTimeBySecond());
            post.save();
            return okJSON200();
        });
    }


}
