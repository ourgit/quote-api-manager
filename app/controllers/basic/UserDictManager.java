package controllers.basic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import constants.BusinessConstant;
import controllers.BaseAdminSecurityController;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import models.admin.AdminMember;
import models.system.Dict;
import models.system.UserDict;
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
 * 菜单管理
 */
public class UserDictManager extends BaseAdminSecurityController {


    /**
     * @api {GET} /v1/cp/user_dict_list/?dictName=&page=&cateName=  01用户字典列表
     * @apiName listUserDict
     * @apiGroup ADMIN-USER-DICT
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200) {JsonArray} list 菜单列表
     * @apiSuccess (Success 200) {long} id id
     * @apiSuccess (Success 200){String} name 名称
     * @apiSuccess (Success 200){long} count 使用频率
     */
    public CompletionStage<Result> listUserDict(Http.Request request, final String dictName, final String cateName, int page) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            if (null == admin) return unauth403();
            ExpressionList<UserDict> expressionList = UserDict.find.query().where().eq("uid", admin.id);
            if (!ValidationUtil.isEmpty(dictName)) expressionList.icontains("dictName", dictName);
            if (!ValidationUtil.isEmpty(cateName)) expressionList.icontains("cateName", cateName);
            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            if (page == 0) {
                List<UserDict> list = expressionList
                        .orderBy().desc("count")
                        .setMaxRows(10)
                        .findList();
                result.set("list", Json.toJson(list));
            } else {
                PagedList<UserDict> pagedList = expressionList
                        .orderBy().desc("count")
                        .setFirstRow((page - 1) * BusinessConstant.PAGE_SIZE_20)
                        .setMaxRows(BusinessConstant.PAGE_SIZE_20)
                        .findPagedList();
                int pages = pagedList.getTotalPageCount();
                List<UserDict> list = pagedList.getList();
                result.set("list", Json.toJson(list));
                result.put("pages", pages);
            }
            return ok(result);
        });
    }

    /**
     * @api {POST} /v1/cp/user_dict/new/ 02使用字典
     * @apiName useDict
     * @apiGroup ADMIN-USER-DICT
     * @apiParam {String} name 字典名
     * @apiParam {String} cateName 字典分类名
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> useDict(Http.Request request) {
        JsonNode requestNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            if (null == admin) return unauth403();
            String name = requestNode.findPath("name").asText();
            String cateName = requestNode.findPath("cateName").asText();
            if (ValidationUtil.isEmpty(name)) return okJSON200();
            Dict dict = Dict.find.query().where().eq("name", name)
                    .orderBy().asc("id")
                    .setMaxRows(1)
                    .findOne();
            if (null != dict) {
                UserDict userDict = UserDict.find.query().where()
                        .eq("uid", admin.id)
                        .eq("dictName", dict.name)
                        .orderBy().asc("id")
                        .setMaxRows(1).findOne();
                if (null == userDict) {
                    userDict = new UserDict();
                    userDict.setCateName(cateName);
                    userDict.setCount(1);
                    userDict.setUid(admin.id);
                    userDict.setDictName(dict.name);
                    userDict.setPinyinAbbr(dict.pinyinAbbr);
                    userDict.save();
                } else {
                    userDict.setCount(userDict.count + 1);
                    userDict.save();
                }
            }
            return okJSON200();
        });
    }


    /**
     * @api {POST} /v1/cp/user_dict/ 03删除用户字典
     * @apiName deleteUserDict
     * @apiGroup ADMIN-USER-DICT
     * @apiParam {int} id 用户字典id
     * @apiParam {String} operation 操作,"del"为删除
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> deleteUserDict(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        String operation = jsonNode.findPath("operation").asText();
        return CompletableFuture.supplyAsync(() -> {
            if (ValidationUtil.isEmpty(operation) || !operation.equals("del")) return okCustomJson(CODE40001, "参数错误");
            long id = jsonNode.findPath("id").asLong();
            if (id < 1) return okCustomJson(CODE40001, "参数错误");
            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            if (null == admin) return unauth403();

            UserDict dict = UserDict.find.query().where()
                    .eq("uid", admin.id)
                    .eq("id", id)
                    .setMaxRows(1).findOne();
            if (null == dict) return okCustomJson(CODE40001, "该用户字典不存在");
            dict.delete();
            return okJSON200();
        });
    }


}
