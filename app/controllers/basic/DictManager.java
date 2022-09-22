package controllers.basic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.BaseAdminSecurityController;
import io.ebean.DB;
import io.ebean.Expr;
import io.ebean.ExpressionList;
import models.admin.AdminMember;
import models.system.Dict;
import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import utils.Pinyin4j;
import utils.ValidationUtil;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * 菜单管理
 */
public class DictManager extends BaseAdminSecurityController {

    @Inject
    Pinyin4j pinyin4j;

    /**
     * @api {GET} /v1/cp/dict/?filter=&parentId=&parentName=  01字典列表
     * @apiName listDict
     * @apiGroup ADMIN-DICT
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200) {JsonArray} list 菜单列表
     * @apiSuccess (Success 200) {long} id id
     * @apiSuccess (Success 200) {int} sort 排序 降序
     * @apiSuccess (Success 200){String} name 名称
     * @apiSuccess (Success 200){String} path 菜单上下级，用于菜单之间的关系
     * @apiSuccess (Success 200){long} parentId 父级菜单ID
     */
    public CompletionStage<Result> listDict(final String filter, int parentId, String parentName) {
        return CompletableFuture.supplyAsync(() -> {
            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            ExpressionList<Dict> expressionList = Dict.find.query().where();
            if (!ValidationUtil.isEmpty(parentName)) {
                Dict parentDict = Dict.find.query().where().eq("name", parentName)
                        .setMaxRows(1).orderBy().asc("id").findOne();
                if (null != parentDict) {
                    result.put("parentId", parentDict.id);
                    expressionList.icontains("path", "/" + parentDict.id + "/");
                }
            }
            if (!ValidationUtil.isEmpty(filter)) {
                expressionList.or(Expr.icontains("name", filter), Expr.icontains("pinyinAbbr", filter));
            }
            if (parentId > 0) {
                expressionList.eq("parentId", parentId);
                result.put("parentId", parentId);
            }
            List<Dict> list = expressionList
                    .orderBy().desc("sort")
                    .orderBy().asc("id")
                    .findList();

            List<Dict> resultList = convertListToTreeNode(list);
            result.set("list", Json.toJson(resultList));
            return ok(result);
        });
    }

    public List<Dict> convertListToTreeNode(List<Dict> dictList) {
        List<Dict> nodeList = new ArrayList<>();
        if (null == dictList) return nodeList;
        for (Dict dict : dictList) {
            boolean mark = false;
            for (Dict node2 : dictList) {
                if (dict.parentId == node2.id) {
                    mark = true;
                    if (node2.children == null)
                        node2.children = new ArrayList<>();
                    node2.children.add(dict);
                    break;
                }
            }
            if (!mark) {
                nodeList.add(dict);
            }
        }
        return nodeList;
    }

    /**
     * @api {GET} /v1/cp/dict/:id/ 02字典详情
     * @apiName getDict
     * @apiGroup ADMIN-DICT
     * @apiSuccess (Success 200) {long} id id
     * @apiSuccess (Success 200) {int} sort 排序 降序
     * @apiSuccess (Success 200){String} name 名称
     * @apiSuccess (Success 200){String} path 菜单上下级，用于菜单之间的关系
     * @apiSuccess (Success 200){long} parentId 父级菜单ID
     */
    public CompletionStage<Result> getDict(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            if (id < 1) return okCustomJson(CODE40001, "参数错误");
            Dict dict = Dict.find.byId(id);
            if (null == dict) return okCustomJson(CODE40002, "该字典不存在");
            List<Dict> children = Dict.find.query().where().eq("parentId", id).findList();
            dict.children = children;
            ObjectNode result = (ObjectNode) Json.toJson(dict);
            result.put(CODE, CODE200);
            return ok(result);
        });
    }

    /**
     * @api {POST} /v1/cp/dict/new/ 03添加字典
     * @apiName addDict
     * @apiGroup ADMIN-DICT
     * @apiParam {int} sort 排序值
     * @apiParam {String} name 分类名称
     * @apiParam {String} attr 属性
     * @apiParam {String} attrValue 属性值
     * @apiParam {long} parentId 父类id
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> addDict(Http.Request request) {
        JsonNode requestNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();

            Dict param = Json.fromJson(requestNode, Dict.class);
            if (ValidationUtil.isEmpty(param.name)) return okCustomJson(CODE40001, "请输入字典名字");
            param.name = param.name.trim();
            param.setPinyinAbbr(pinyin4j.toPinYinUppercase(param.name));
            Dict nameDict = Dict.find.query().where()
                    .eq("parentId", param.parentId)
                    .eq("name", param.name).setMaxRows(1).findOne();
            if (null != nameDict) return okCustomJson(CODE40001, "该层级的字典已存在");

            Dict parentMerchantCategory = null;
            if (param.parentId > 0) {
                parentMerchantCategory = Dict.find.byId(param.parentId);
                if (null == parentMerchantCategory) return okCustomJson(CODE40001, "父类ID不存在");
            }
            if (null != parentMerchantCategory) {
                String parentPath = parentMerchantCategory.path;
                if (ValidationUtil.isEmpty(parentPath)) parentPath = "/";
                param.setPath(parentPath + parentMerchantCategory.id + "/");
            } else param.setPath("/");

            long currentTime = dateUtils.getCurrentTimeBySecond();
            if (param.parentId == 0) param.parentId = 1;
            param.setCreateTime(currentTime);
            param.save();
            businessUtils.addOperationLog(request, admin, "修改字典：" + requestNode.toString());
            ObjectNode resultNode = (ObjectNode) Json.toJson(param);
            resultNode.put(CODE, CODE200);
            return ok(resultNode);
        });
    }

    /**
     * @api {POST} /v1/cp/dict/:id/ 04修改字典
     * @apiName updateDict
     * @apiGroup ADMIN-DICT
     * @apiParam {String} [name] 分类名称
     * @apiParam {String} [attr] 属性
     * @apiParam {String} [attrValue] 属性值
     * @apiParam {long} [parentId] 父类id
     * @apiParam {int} [sort] 排序值
     * @apiParam {long} [prependDictId] 欲安插的字典ID
     * @apiParam {String} [place] before/after 前面或后面
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> updateDict(Http.Request request, long id) {
        JsonNode requestNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            if (id < 1) return okCustomJson(CODE40001, "参数错误");
            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();

            Dict dict = Dict.find.byId(id);
            if (null == dict) return okCustomJson(CODE40002, "该字典不存在");
            Dict param = Json.fromJson(requestNode, Dict.class);
            if (null == param) return okCustomJson(CODE40001, "参数错误");
            if (id == param.parentId) return okCustomJson(CODE40002, "子字典不能跟父字典一样");
            if (requestNode.has("parentId") && param.parentId > 0) {
                setDictPath(dict, param.parentId);
            }
            if (param.sort > 0) dict.setSort(param.sort);
            if (requestNode.has("lat")) dict.setLat(param.lat);
            if (requestNode.has("lng")) dict.setLng(param.lng);
            if (requestNode.has("attr")) dict.setAttr(param.attr);
            if (requestNode.has("attrValue")) dict.setAttrValue(param.attrValue);
            if (!ValidationUtil.isEmpty(param.name)) {
                Dict titleMenu = Dict.find.query().where()
                        .eq("name", param.name)
                        .eq("parentId", dict.parentId)
                        .ne("id", dict.id)
                        .setMaxRows(1).findOne();
                if (null != titleMenu) return okCustomJson(CODE40001, "该字典名字已存在");
                dict.setName(param.name.trim());
                dict.setPinyinAbbr(pinyin4j.toPinYinUppercase(param.name));
            }
            long prependDictId = requestNode.findPath("prependDictId").asLong();
            String place = requestNode.findPath("place").asText();
            if (prependDictId > 0 && !ValidationUtil.isEmpty(place)) {
                Dict prependDict = Dict.find.byId(prependDictId);
                if (null != prependDict) {
                    int sort;
                    if (place.equalsIgnoreCase("before")) {
                        sort = prependDict.sort + 1;
                    } else {
                        sort = prependDict.sort - 1;
                    }
                    dict.setSort(sort);
                }
            }
            dict.save();
            businessUtils.addOperationLog(request, admin, "修改字典：" + requestNode.toString());
            return okJSON200();
        });
    }

    private void setDictPath(Dict dict, long parentId) {
        if (parentId > -1) {
            dict.setParentId(parentId);
            if (parentId > 0) {
                Dict parentMenu = Dict.find.byId(parentId);
                if (null != parentMenu) {
                    String parentPath = parentMenu.path;
                    if (ValidationUtil.isEmpty(parentPath)) parentPath = "/";
                    dict.setPath(parentPath + parentMenu.id + "/");
                }
            } else dict.setPath("/");
        }
    }

    /**
     * @api {POST} /v1/cp/dict/ 05删除字典
     * @apiName deleteDict
     * @apiGroup ADMIN-DICT
     * @apiParam {int} id 字典id
     * @apiParam {String} operation 操作,"del"为删除
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     * @apiSuccess (Error 40002) {int} code 40002 该字典不存在
     * @apiSuccess (Error 40003) {int} code 40003 该字典为父级分类,不能直接删除
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> deleteDict(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        String operation = jsonNode.findPath("operation").asText();
        return CompletableFuture.supplyAsync(() -> {
            if (ValidationUtil.isEmpty(operation) || !operation.equals("del")) return okCustomJson(CODE40001, "参数错误");
            long id = jsonNode.findPath("id").asLong();
            if (id < 1) return okCustomJson(CODE40001, "参数错误");
            Dict dict = Dict.find.byId(id);
            if (null == dict) return okCustomJson(CODE40001, "该字典不存在");

            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();

            Dict subDict = Dict.find.query().where().eq("parentId", dict.id).setMaxRows(1).findOne();
            if (null != subDict) return okCustomJson(CODE40003, "该字典为父级,不能直接删除");
            dict.delete();
            businessUtils.addOperationLog(request, admin, "删除字典：" + jsonNode.toString());
            return okJSON200();
        });
    }

    public CompletionStage<Result> updateDictPath(Http.Request request) {
        return CompletableFuture.supplyAsync(() -> {
            List<Dict> list = Dict.find.query().orderBy().asc("id").findList();
            list.forEach((each) -> {
                if (null != each) {
                    List<Dict> children = Dict.find.query().where().eq("parentId", each.id).findList();
                    List<Dict> updateList = new ArrayList<>();
                    children.forEach((child) -> {
                        if (child != null) {
                            child.setPath(each.path + each.id + "/");
                            updateList.add(child);
                        }
                    });
                    if (updateList.size() > 0) DB.saveAll(updateList);
                }
            });
            return okJSON200();
        });
    }


}
