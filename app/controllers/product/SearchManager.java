package controllers.product;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.BaseAdminSecurityController;
import models.product.SearchKeyword;
import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import utils.ValidationUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


public class SearchManager extends BaseAdminSecurityController {

    /**
     * @api {GET} /v1/cp/search_key_word/ 01搜索关键字列表
     * @apiName listSearchKeyWords
     * @apiGroup ADMIN-SEARCH-KEYWORDS
     * @apiSuccess (Success 200) {String} id id
     * @apiSuccess (Success 200) {String} keyword 关键字
     * @apiSuccess (Success 200){int} code 200
     */
    public CompletionStage<Result> listSearchKeyWords() {
        return CompletableFuture.supplyAsync(() -> {
            List<SearchKeyword> list = SearchKeyword.find.query().orderBy().desc("sort").findList();
            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            result.set("list", Json.toJson(list));
            return ok(result);
        });
    }

    /**
     * @api {POST} /v1/cp/search_key_word/new/ 02新增搜索关键字
     * @apiName addSearchKeyWord
     * @apiGroup ADMIN-SEARCH-KEYWORDS
     * @apiParam {String} keyword 关键字
     * @apiParam {int} from 1首页　２店铺
     * @apiSuccess (Success 200){int} code 200
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> addSearchKeyWord(Http.Request request) {
        return CompletableFuture.supplyAsync(() -> {
            JsonNode jsonNode = request.body().asJson();
            if (null == jsonNode) return okCustomJson(CODE40001, "参数错误");
            SearchKeyword param = Json.fromJson(jsonNode, SearchKeyword.class);
            if (null == param) return okCustomJson(CODE40001, "参数错误");
            if (param.from < 1) param.from = 1;
            if (param.sort < 1) param.sort = 1;
            SearchKeyword exist = SearchKeyword.find.query().where()
                    .eq("keyword", param.keyword)
                    .setMaxRows(1)
                    .findOne();
            if (null != exist) return okCustomJson(CODE40002, "该关键词已存在");
            param.setEnable(true);
            param.save();
            updateSearchKeyCache();
            return okJSON200();
        });
    }

    /**
     * @api {POST} /v1/cp/search_key_word/ 03删除搜索关键字
     * @apiName deleteSearchKeyWord
     * @apiGroup ADMIN-SEARCH-KEYWORDS
     * @apiParam {int} id 运费id
     * @apiParam {String} operation del为删除
     * @apiSuccess (Success 200){int} code 200
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> deleteSearchKeyWord(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            String operation = jsonNode.findPath("operation").asText();
            if (ValidationUtil.isEmpty(operation) || !operation.equals("del")) return okCustomJson(CODE40001, "参数错误");
            long id = jsonNode.findPath("id").asInt();
            if (id < 1) return okCustomJson(CODE40001, "参数错误");
            SearchKeyword searchKeyWord = SearchKeyword.find.byId(id);
            if (null == searchKeyWord) return okCustomJson(CODE40002, "该搜索关键字不存在");
            searchKeyWord.delete();
            updateSearchKeyCache();
            return okJSON200();
        });
    }

    private void updateSearchKeyCache() {
        String jsonCacheKey = cacheUtils.getSearchKeywordsJsonCache();
        syncCache.remove(jsonCacheKey);
    }

}
