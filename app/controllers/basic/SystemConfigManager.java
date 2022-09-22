package controllers.basic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import constants.BusinessConstant;
import constants.RedisKeyConstant;
import controllers.BaseAdminSecurityController;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import models.admin.AdminMember;
import models.system.SmsTemplate;
import models.system.SystemCarousel;
import models.system.SystemLink;
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
 * 系统配置管理类
 */
public class SystemConfigManager extends BaseAdminSecurityController {
    /**
     * @api {GET} /v1/cp/carousels/?page=&filter=&queryType=&bizType 01查看轮播列表
     * @apiName listCarousel
     * @apiGroup ADMIN-SYSTEM-CONFIG
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess {JsonArray} list 列表
     * @apiSuccess {int} id 轮播id
     * @apiSuccess {string} name 轮播名称
     * @apiSuccess {string} imgUrl 图片链接地址
     * @apiSuccess {string} linkUrl 链接地址
     * @apiSuccess {string} description 备注
     * @apiSuccess {string} regionCode 区域编号
     * @apiSuccess {string} regionName 区域名字
     * @apiSuccess {int} sort 显示顺序
     * @apiSuccess (Error 500) {int} code 500 未知错误
     */
    public CompletionStage<Result> listCarousel(int page, String filter, int queryType, int bizType) {
        return CompletableFuture.supplyAsync(() -> {
            ExpressionList<SystemCarousel> expressionList = SystemCarousel.find.query().where();
            if (queryType > 0) expressionList.eq("type", queryType);
            if (bizType > 0) expressionList.eq("bizType", bizType);
            if (!ValidationUtil.isEmpty(filter)) expressionList.icontains("name", filter);
            PagedList<SystemCarousel> pagedList = expressionList
                    .setFirstRow((page - 1) * BusinessConstant.PAGE_SIZE_20)
                    .setMaxRows(BusinessConstant.PAGE_SIZE_20)
                    .findPagedList();
            int pages = pagedList.getTotalPageCount();
            List<SystemCarousel> resultList = pagedList.getList();
            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            result.set("list", Json.toJson(resultList));
            result.put("pages", pages);
            return ok(result);
        });

    }

    /**
     * @api {GET} /v1/cp/carousels/:id/ 02查看轮播详情
     * @apiName getCarousel
     * @apiGroup ADMIN-SYSTEM-CONFIG
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess {int} id 轮播id
     * @apiSuccess {string} name 轮播名称
     * @apiSuccess {string} imgUrl 图片链接地址
     * @apiSuccess {string} linkUrl 链接地址
     * @apiSuccess {string} description 备注
     * @apiSuccess {int} sort 显示顺序
     * @apiSuccess {string} regionCode 区域编号
     * @apiSuccess {string} regionName 区域名字
     * @apiSuccess (Error 500) {int} code 500 未知错误
     */
    public CompletionStage<Result> getCarousel(int id) {
        return CompletableFuture.supplyAsync(() -> {
            if (id < 1) return okCustomJson(CODE40001, "参数错误");
            SystemCarousel link = SystemCarousel.find.query().where().eq("id", id).findOne();
            ObjectNode node = (ObjectNode) Json.toJson(link);
            node.put(CODE, CODE200);
            return ok(node);
        });
    }

    /**
     * @api {POST} /v1/cp/carousels/new/ 03添加轮播
     * @apiName addCarousel
     * @apiGroup ADMIN-SYSTEM-CONFIG
     * @apiParam {string} name 轮播名称
     * @apiParam {string} imgUrl 图片链接地址
     * @apiParam {string} linkUrl 链接地址
     * @apiParam {string} description 备注
     * @apiParam {int} sort 显示顺序
     * @apiParam {int} clientType 类型，1PC端，2手机端
     * @apiParam {int} bizType 业务类型，1首页轮播，2商城轮播
     * @apiParam {string} regionCode 区域编号
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> addCarousel(Http.Request request) {
        JsonNode requestNode = request.body().asJson();
        Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
        return CompletableFuture.supplyAsync(() -> {
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            if (null == requestNode) return okCustomJson(CODE40001, "参数错误");
            SystemCarousel carousel = Json.fromJson(requestNode, SystemCarousel.class);
            if (null == carousel) return okCustomJson(CODE40001, "参数错误");
            if (ValidationUtil.isEmpty(carousel.getImgUrl()) && ValidationUtil.isEmpty(carousel.getMobileImgUrl()))
                return okCustomJson(CODE40001, "请上传图片");
            long currentTime = dateUtils.getCurrentTimeBySecond();
            carousel.setUpdateTime(currentTime);
            carousel.setCreatedTime(currentTime);
            carousel.setNeedShow(true);
            carousel.save();
            //更新缓存
            cacheUtils.deleteArticleCache();
            businessUtils.addOperationLog(request, admin, "添加轮播图：" + carousel.toString());
            return okJSON200();
        });
    }

    private void updateArticleCache() {
        syncCache.remove(RedisKeyConstant.CAROUSEL_JSON_CACHE);
        syncCache.remove(RedisKeyConstant.KEY_HOME_PAGE_BOTTOM_INFO_LINKS);
        syncCache.remove(RedisKeyConstant.KEY_HOME_PAGE_INFO_LINKS);
        syncCache.remove(RedisKeyConstant.KEY_ARTICLE_FOR_NEWBIE);
        syncCache.remove(RedisKeyConstant.KEY_CAROUSEL_PREFIX + SystemCarousel.TYPE_PC);
        syncCache.remove(RedisKeyConstant.KEY_CAROUSEL_PREFIX + SystemCarousel.TYPE_MOBILE);
    }

    /**
     * @api {POST} /v1/cp/carousels/:id/ 04更新轮播
     * @apiName updateCarousel
     * @apiGroup ADMIN-SYSTEM-CONFIG
     * @apiParam {string} name 轮播名称
     * @apiParam {string} imgUrl 图片链接地址
     * @apiParam {string} linkUrl 链接地址
     * @apiParam {string} description 备注
     * @apiParam {int} sort 显示顺序
     * @apiParam {string} regionCode 区域编号
     * @apiParam {int} clientType 类型，1PC端，2手机端
     * @apiParam {int} bizType 业务类型，1首页轮播，2商城轮播
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> updateCarousel(Http.Request request, int id) {
        JsonNode requestNode = request.body().asJson();
        Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
        return CompletableFuture.supplyAsync(() -> {
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();

            if (id < 1) return okCustomJson(CODE40001, "参数错误");
            if (null == requestNode) return okCustomJson(CODE40001, "参数错误");
            SystemCarousel param = Json.fromJson(requestNode, SystemCarousel.class);
            if (null == param) return okCustomJson(CODE40001, "参数错误");
            SystemCarousel carousel = SystemCarousel.find.byId(id);
            if (null == carousel) return okCustomJson(CODE40002, "未找到该轮播");
            if (!ValidationUtil.isEmpty(param.getName())) carousel.setName(param.getName());
            if (requestNode.has("imgUrl")) carousel.setImgUrl(param.getImgUrl());
            if (requestNode.has("linkUrl")) carousel.setLinkUrl(param.getLinkUrl());
            if (param.getClientType() > 0) carousel.setClientType(param.getClientType());
            if (param.getBizType() > 0) carousel.setBizType(param.getBizType());

            if (!ValidationUtil.isEmpty(param.getMobileImgUrl())) carousel.setMobileImgUrl(param.getMobileImgUrl());
            if (!ValidationUtil.isEmpty(param.getMobileLinkUrl())) carousel.setMobileLinkUrl(param.getMobileLinkUrl());
            if (!ValidationUtil.isEmpty(param.getDescription())) carousel.setDescription(param.getDescription());
            if (param.getDisplayOrder() > 0) carousel.setDisplayOrder(param.getDisplayOrder());
            if (requestNode.has("needShow")) carousel.setNeedShow(param.isNeedShow());
            if (!ValidationUtil.isEmpty(param.getTitle1())) carousel.setTitle1(param.getTitle1());
            if (!ValidationUtil.isEmpty(param.getTitle2())) carousel.setTitle2(param.getTitle2());
            if (param.getClientType() > 0) carousel.setClientType(param.getClientType());
            carousel.setUpdateTime(dateUtils.getCurrentTimeBySecond());
            carousel.save();
            cacheUtils.deleteArticleCache();
            businessUtils.addOperationLog(request, admin, "修改轮播图，修改前：" + param.toString() + "，修改后" + carousel.toString());
            return okJSON200();
        });
    }

    /**
     * @api {POST} /v1/cp/carousels/ 05删除轮播
     * @apiName delCarousel
     * @apiGroup ADMIN-SYSTEM-CONFIG
     * @apiParam {int} id 轮播id
     * @apiParam {String} operation 操作,"del"为删除
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     * @apiSuccess (Error 40001) {int} code 40002 轮播不存在
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> delCarousel(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
        return CompletableFuture.supplyAsync(() -> {
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();

            String operation = jsonNode.findPath("operation").asText();
            if (ValidationUtil.isEmpty(operation) || !operation.equals("del")) return okCustomJson(CODE40001, "参数错误");
            int id = jsonNode.findPath("id").asInt();
            if (id < 1) return okCustomJson(CODE40001, "参数错误");
            SystemCarousel carousel = SystemCarousel.find.byId(id);
            if (null == carousel) return okCustomJson(CODE40002, "该轮播不存在");
            businessUtils.addOperationLog(request, admin, "删除轮播" + carousel.toString());

            carousel.delete();
            cacheUtils.deleteArticleCache();
            //更新缓存
            updateArticleCache();
            return okJSON200();
        });
    }


    /**
     * @api {GET} /v1/cp/friend_links/?page=&filter= 06查看友情列表
     * @apiName listFriendLinks
     * @apiGroup ADMIN-SYSTEM-CONFIG
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess {JsonArray} list 列表
     * @apiSuccess {int} id 友链id
     * @apiSuccess {string} name 友链名字
     * @apiSuccess {string} url 友链url
     * @apiSuccess {int} sort 显示顺序
     * @apiSuccess {int} status 显示顺序 1显示 2隐藏
     * @apiSuccess {string} description 描述
     * @apiSuccess (Error 500) {int} code 500 未知错误
     */
    public Result listFriendLinks(int page, String filter) {
        if (page < 1) page = 1;
        PagedList<SystemLink> pageList;
        if (ValidationUtil.isEmpty(filter)) {
            pageList = SystemLink.find.query()
                    .setFirstRow((page - 1) * BusinessConstant.PAGE_SIZE_20)
                    .setMaxRows(BusinessConstant.PAGE_SIZE_20)
                    .findPagedList();
        } else {
            pageList = SystemLink.find.query().where().icontains("name", filter)
                    .setFirstRow((page - 1) * BusinessConstant.PAGE_SIZE_20)
                    .setMaxRows(BusinessConstant.PAGE_SIZE_20)
                    .findPagedList();
        }
        int pages = pageList.getTotalPageCount();
        List<SystemLink> resultList = pageList.getList();
        ObjectNode result = Json.newObject();
        result.put(CODE, CODE200);
        result.set("list", Json.toJson(resultList));
        result.put("pages", pages);
        return ok(result);
    }

    /**
     * @api {GET} /v1/cp/friend_links/:linkId/ 07查看具体友链
     * @apiName getFriendLink
     * @apiGroup ADMIN-SYSTEM-CONFIG
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess {int} id 友链id
     * @apiSuccess {string} name 友链名字
     * @apiSuccess {string} url 友链url
     * @apiSuccess {int} sort 显示顺序
     * @apiSuccess {int} status 显示顺序 1显示 2隐藏
     * @apiSuccess {string} description 描述
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     */
    public Result getFriendLink(int linkId) {
        if (linkId < 1) return okCustomJson(CODE40001, "参数错误");
        SystemLink link = SystemLink.find.query().where().eq("id", linkId).findOne();
        ObjectNode node = (ObjectNode) Json.toJson(link);
        node.put(CODE, CODE200);
        return ok(node);
    }

    /**
     * @api {POST} /v1/cp/friend_links/new/ 08添加友链
     * @apiName addFriendLink
     * @apiGroup ADMIN-SYSTEM-CONFIG
     * @apiParam {string} name 友链名字
     * @apiParam {string} url 友链url
     * @apiParam {int} sort 显示顺序
     * @apiParam {int} status 显示顺序 1显示 2隐藏
     * @apiParam {string} description 描述
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> addFriendLink(Http.Request request) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            JsonNode requestNode = request.body().asJson();

            if (null == requestNode) return okCustomJson(CODE40001, "参数错误");
            SystemLink link = Json.fromJson(requestNode, SystemLink.class);
            if (null == link) return okCustomJson(CODE40001, "参数错误");
            if (ValidationUtil.isEmpty(link.name) || ValidationUtil.isEmpty(link.url))
                return okCustomJson(CODE40001, "参数错误");
            long currentTime = dateUtils.getCurrentTimeBySecond();
            link.setUpdateTime(currentTime);
            link.setCreatedTime(currentTime);
            link.save();
            //更新缓存
            updateArticleCache();
            businessUtils.addOperationLog(request, admin, "添加友链" + link.toString());
            return okJSON200();
        });

    }

    /**
     * @api {POST} /v1/cp/friend_links/:linkId/ 09更新友链
     * @apiName updateFriendLink
     * @apiGroup ADMIN-SYSTEM-CONFIG
     * @apiParam {string} name 友链名字
     * @apiParam {string} url 友链url
     * @apiParam {int} sort 显示顺序
     * @apiParam {int} status 显示顺序 1显示 2隐藏
     * @apiParam {string} description 描述
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> updateFriendLink(Http.Request request, int linkId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            JsonNode requestNode = request.body().asJson();
            if (linkId < 1) return okCustomJson(CODE40001, "参数错误");
            if (null == requestNode) return okCustomJson(CODE40001, "参数错误");
            SystemLink link = Json.fromJson(requestNode, SystemLink.class);
            if (null == link) return okCustomJson(CODE40001, "参数错误");
            SystemLink updateLink = SystemLink.find.byId(linkId);
            if (null == updateLink) return okCustomJson(CODE40002, "未找到该友链");

            businessUtils.addOperationLog(request, admin, "修改轮友链，执行前" + updateLink.toString() + ";修改的参数：" + link.toString());

            if (!ValidationUtil.isEmpty(link.name) && !link.name.equals(updateLink.name)) updateLink.setName(link.name);
            if (!ValidationUtil.isEmpty(link.url) && !link.url.equals(updateLink.url)) updateLink.setUrl(link.url);
            if (!ValidationUtil.isEmpty(link.description) && !link.description.equals(updateLink.description))
                updateLink.setDescription(link.description);
            if (link.sort > 0 && link.sort != updateLink.sort)
                updateLink.setSort(link.sort);
            if (link.status > 0 && link.status != updateLink.status) updateLink.setStatus(link.status);
            updateLink.setUpdateTime(dateUtils.getCurrentTimeBySecond());
            updateLink.save();
            //更新缓存
            updateArticleCache();
            return okJSON200();
        });

    }

    /**
     * @api {POST} /v1/cp/friend_links/ 10删除友链
     * @apiName delFriendLink
     * @apiGroup ADMIN-SYSTEM-CONFIG
     * @apiParam {int} id 友链id
     * @apiParam {String} operation 操作,"del"为删除
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     * @apiSuccess (Error 40001) {int} code 40002 友链不存在
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> delFriendLink(Http.Request request) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            JsonNode jsonNode = request.body().asJson();
            String operation = jsonNode.findPath("operation").asText();
            if (ValidationUtil.isEmpty(operation) || !operation.equals("del")) return okCustomJson(CODE40001, "参数错误");
            int id = jsonNode.findPath("id").asInt();
            if (id < 1) return okCustomJson(CODE40001, "参数错误");
            SystemLink updateLink = SystemLink.find.byId(id);
            if (null == updateLink) return okCustomJson(CODE40002, "该友链不存在");
            businessUtils.addOperationLog(request, admin, "删除友链" + updateLink.toString());
            updateLink.delete();
            //更新缓存
            updateArticleCache();
            return okJSON200();
        });

    }


    /**
     * @api {GET} /v1/cp/sms_template_list/ 11短信模板列表
     * @apiName listSmsTemplate
     * @apiGroup ADMIN-SYSTEM-CONFIG
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess {JsonArray} list 列表
     * @apiSuccess {int} id id
     * @apiSuccess {long} id id
     * @apiSuccess {string} templateId 第三方模板ID
     * @apiSuccess {string} content 模板
     * @apiSuccess (Error 500) {int} code 500 未知错误
     */
    public CompletionStage<Result> listSmsTemplate() {
        return CompletableFuture.supplyAsync(() -> {
            List<SmsTemplate> list = SmsTemplate.find.query().where()
                    .eq("enable", true)
                    .orderBy().asc("id")
                    .findList();
            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            result.set("list", Json.toJson(list));
            return ok(result);
        });
    }

    /**
     * @api {GET} /v1/cp/sms_template_list/:id/ 12短信模板详情
     * @apiName getSmsTemplate
     * @apiGroup ADMIN-SYSTEM-CONFIG
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess {int} id id
     * @apiSuccess {long} id id
     * @apiSuccess {string} templateId 第三方模板ID
     * @apiSuccess {string} content 模板
     * @apiSuccess (Error 500) {int} code 500 未知错误
     */
    public CompletionStage<Result> getSmsTemplate(long id) {
        return CompletableFuture.supplyAsync(() -> {
            SmsTemplate smsTemplate = SmsTemplate.find.byId(id);
            ObjectNode result = (ObjectNode) Json.toJson(smsTemplate);
            result.put(CODE, CODE200);
            return ok(result);
        });
    }

}
