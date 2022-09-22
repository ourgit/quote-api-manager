package controllers.poster;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.BaseAdminSecurityController;
import io.ebean.DB;
import io.ebean.ExpressionList;
import models.poster.Poster;
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

/**
 * 海报管理
 */
public class PosterManager extends BaseAdminSecurityController {
    /**
     * @api {GET} /v1/cp/poster/?beginTime=&endTime 01查看海报列表
     * @apiName listPoster
     * @apiGroup ADMIN-POSTER
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200) {JsonObject} list 列表
     * @apiSuccess (Success 200){long} id id
     * @apiSuccess (Success 200){String} imgUrl imgUrl
     * @apiSuccess (Success 200){long} publishDate publishDate
     * @apiSuccess (Success 200){long} createTime createTime
     */
    public CompletionStage<Result> listPoster(long beginTime, long endTime) {
        return CompletableFuture.supplyAsync(() -> {
            ExpressionList<Poster> expressionList = Poster.find.query().where();
            if (beginTime > 0) expressionList.ge("publishDate", beginTime);
            if (endTime > 0) expressionList.le("publishDate", endTime);
            List<Poster> list = expressionList.orderBy().asc("id").findList();
            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            result.set("list", Json.toJson(list));
            return ok(result);
        });
    }

    /**
     * @api {POST} /v1/cp/poster/new/ 02添加海报
     * @apiName addPoster
     * @apiGroup ADMIN-POSTER
     * @apiParam {String} imgUrl imgUrl
     * @apiParam {long} publishDate publishDate
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> addPoster(Http.Request request) {
        JsonNode requestNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            if (null == requestNode) return okCustomJson(CODE40001, "参数错误");
            if (!requestNode.has("list")) return okCustomJson(CODE40001, "list为空");
            ArrayNode nodes = (ArrayNode) requestNode.findPath("list");
            if (nodes.size() < 1) return okCustomJson(CODE40001, "list为空");
            List<Poster> updateList = new ArrayList<>();
            for (int i = 0; i < nodes.size(); i++) {
                JsonNode node = nodes.get(i);
                Poster poster = Json.fromJson(node, Poster.class);
                if (null != poster) {
                    if (ValidationUtil.isEmpty(poster.imgUrl)) return okCustomJson(CODE40001, "请上传海报图");
                    long currentTime = dateUtils.getCurrentTimeBySecond();
                    if (poster.publishDate < 1) return okCustomJson(CODE40001, "请选择海报发布时间");
                    long minTime = dateUtils.getTodayMinTimestamp();
                    if (poster.publishDate < minTime) return okCustomJson(CODE40001, "海报发布时间只能选择今天之后");
                    poster.setCreateTime(currentTime);
                    updateList.add(poster);
                }
            }
            if (updateList.size() < 1) return okCustomJson(CODE40001, "可保存的海报列表为空，请检查参数");
            DB.saveAll(updateList);
            return okJSON200();
        });
    }

    /**
     * @api {POST} /v1/cp/poster/ 03删除海报
     * @apiName delPoster
     * @apiGroup ADMIN-POSTER
     * @apiParam {Array} list 海报id数组
     * @apiParam {String} operation 操作,"del"为删除
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     * @apiSuccess (Error 40002) {int} code 40002 该海报不存在
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> delPoster(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            String operation = jsonNode.findPath("operation").asText();
            if (ValidationUtil.isEmpty(operation) || !operation.equals("del")) return okCustomJson(CODE40001, "请求参数错误");
            if (!jsonNode.has("list")) return okCustomJson(CODE40001, "ID列表为空");
            ArrayNode list = (ArrayNode) jsonNode.findPath("list");
            if (list.size() < 1) return okCustomJson(CODE40001, "ID列表为空");
            List<Poster> deleteList = new ArrayList<>();
            list.forEach((each) -> {
                long id = each.asLong();
                Poster poster = Poster.find.byId(id);
                if (null != poster) {
                    deleteList.add(poster);
                }
            });
            if (deleteList.size() > 0) DB.deleteAll(deleteList);
            return okJSON200();
        });
    }

}
