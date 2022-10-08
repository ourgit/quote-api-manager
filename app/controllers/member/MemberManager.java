package controllers.member;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import constants.BusinessConstant;
import controllers.BaseAdminSecurityController;
import io.ebean.Expr;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import models.admin.AdminMember;
import models.log.BalanceLog;
import models.user.*;
import myannotation.EscapeHtmlSerializer;
import play.Logger;
import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import utils.*;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static constants.BusinessConstant.*;
import static utils.BusinessItem.CASH;

/**
 * 用户管理
 */
public class MemberManager extends BaseAdminSecurityController {
    Logger.ALogger logger = Logger.of(MemberManager.class);

    @Inject
    EscapeHtmlSerializer escapeHtmlSerializer;

    /**
     * @api {POST} /v1/cp/members/?page=&uid=&filter= 01获取用户列表
     * @apiName listMembers
     * @apiGroup ADMIN_MEMBER
     * @apiParam {long} uid uid
     * @apiParam {int} page page
     * @apiParam {int} hasDealer 0全部 1有 2没有
     * @apiParam {String} filter realName/nickName/phoneNumber/dealerCode
     * @apiParam {int} status 0all 1normal 2pause
     * @apiSuccess (Success 200){int} code 200
     * @apiSuccess (Success 200){int} pages 分页
     * @apiSuccess (Success 200){JsonArray} list 用户列表
     * @apiSuccess (Success 200){long} id 用户ID
     * @apiSuccess (Success 200){int} status 用户状态1正常2锁定
     * @apiSuccess (Success 200){string} realName 实名
     * @apiSuccess (Success 200){string} nickName 昵称
     * @apiSuccess (Success 200){string} phoneNumber 手机号
     * @apiSuccess (Success 200){string} description 备注
     * @apiSuccess (Success 200){string} agentCode 代理编号
     * @apiSuccess (Success 200){string} updateTime 更新时间
     * @apiSuccess (Success 200){string} createdTime 创建时间
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> listMembers(Http.Request request) {
        return CompletableFuture.supplyAsync(() -> {
            JsonNode requestNode = request.body().asJson();
            if (null == requestNode) return okCustomJson(CODE40001, "参数错误");
            int page = requestNode.findPath("page").asInt();
            int status = requestNode.findPath("status").asInt();
            int hasDealer = requestNode.findPath("hasDealer").asInt();
            long uid = requestNode.findPath("uid").asLong();
            long dealerId = requestNode.findPath("dealerId").asLong();
            long shopId = requestNode.findPath("shopId").asLong();

            String filter = requestNode.findPath("filter").asText();
            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            ExpressionList<Member> expressionList = Member.find.query().where();
            if (uid > 0) expressionList.eq("uid", uid);
//            if (status > 0) expressionList.eq("status", status);
            if (dealerId > 0) expressionList.eq("dealerId", dealerId);
            if (shopId > 0) expressionList.eq("shopId", shopId);
            if (hasDealer > 0) {
                if (hasDealer == 1) expressionList.gt("dealerId", 0);
                else expressionList.eq("dealerId", 0);
            }
            if (requestNode.has("shopIdList")) {
                ArrayNode orgIdList = (ArrayNode) requestNode.findPath("shopIdList");
                Set<Long> orgIdSet = new HashSet<>();
                orgIdList.forEach((each) -> orgIdSet.add(each.asLong()));
                if (orgIdSet.size() > 0) expressionList.in("shopId", orgIdSet);
            }

            if (!ValidationUtil.isEmpty(filter)) {
                String orFilter = escapeHtmlSerializer.escapeHtml(filter);
                orFilter = "%" + orFilter + "%";
                expressionList.or(
                        Expr.or(Expr.ilike("realName", orFilter), Expr.like("contactPhoneNumber", orFilter)),
                        Expr.or(Expr.ilike("nickName", orFilter), Expr.ilike("selfCode", orFilter))
                );
            }
            int members = expressionList.findCount();
            PagedList<Member> pagedList = expressionList.orderBy().desc("id")
                    .setFirstRow((page - 1) * BusinessConstant.PAGE_SIZE_20)
                    .setMaxRows(BusinessConstant.PAGE_SIZE_20)
                    .findPagedList();
            List<Member> list = pagedList.getList();
            ArrayNode nodes = Json.newArray();
            String month = dateUtils.getCurrentMonth();
            list.forEach((each) -> {

                long orders = 0;
                double totalOrderMoney = 0;
                ObjectNode memberNode = (ObjectNode) Json.toJson(each);
                memberNode.put("orders", orders);
                memberNode.put("totalOrderMoney", DF_TWO_DIGIT.format(totalOrderMoney));
                List<MemberBalance> balances = MemberBalance.find.query().where().eq("uid", each.id)
                        .orderBy().asc("itemId")
                        .findList();
                memberNode.set("balances", Json.toJson(balances));
                nodes.add(memberNode);

            });
            int pages = pagedList.getTotalPageCount();

            ObjectNode node = Json.newObject();
            node.put(CODE, CODE200);
            node.put("pages", pages);
            node.put("totalMembers", members);
            node.set("list", nodes);
            businessUtils.addOperationLog(request, admin, "执行查询用户列表");
            return ok(node);
        });
    }

    /**
     * @api {GET} /v1/cp/members/:memberId/ 02获取用户详情
     * @apiName getUser
     * @apiGroup ADMIN_MEMBER
     * @apiSuccess (Success 200){int} code 200
     * @apiSuccess (Success 200){int} pages 分页
     * @apiSuccess (Success 200){JsonArray} list 用户列表
     * @apiSuccess (Success 200){long} id 用户ID
     * @apiSuccess (Success 200){int} status 用户状态1正常2锁定
     * @apiSuccess (Success 200){string} realName 实名
     * @apiSuccess (Success 200){string} nickName 昵称
     * @apiSuccess (Success 200){string} phoneNumber 手机号
     * @apiSuccess (Success 200){string} description 备注
     * @apiSuccess (Success 200){long} birthday 生日
     * @apiSuccess (Success 200){String} idCardNo 身份证号
     * @apiSuccess (Success 200){String} licenseNo 营业执照
     * @apiSuccess (Success 200){String} licenseImgUrl 营业执照图片地址
     * @apiSuccess (Success 200){string} agentCode 代理编号
     * @apiSuccess (Success 200){string} idCardNo 身份证号码
     * @apiSuccess (Success 200){string} licenseNo 营业执照
     * @apiSuccess (Success 200){int} gender 0：未知、1：男、2：女
     * @apiSuccess (Success 200){String} city 城市
     * @apiSuccess (Success 200){String} province 省份
     * @apiSuccess (Success 200){String} country 国家
     * @apiSuccess (Success 200){String} shopName 店铺
     * @apiSuccess (Success 200){String} contactPhoneNumber 联系电话
     * @apiSuccess (Success 200){String} contactAddress 联系地址
     * @apiSuccess (Success 200){String} businessItems 经营类目
     * @apiSuccess (Success 200){String} images 图片，多张，以逗号隔开
     * @apiSuccess (Success 200){string} createdTime 创建时间
     */
    public CompletionStage<Result> getMember(Http.Request request, long uid) {
        Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
        return CompletableFuture.supplyAsync(() -> {
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            Member member = Member.find.byId(uid);
            if (null == member) return okCustomJson(CODE40001, "用户不存在");
            ObjectNode result = (ObjectNode) Json.toJson(member);
            List<MemberBalance> list = MemberBalance.find.query().where().eq("uid", uid).findList();
            result.put(CODE, CODE200);
            result.set("balanceList", Json.toJson(list));
            businessUtils.addOperationLog(request, admin, "查看用户详情，uid:" + member.id);
            return ok(result);
        });
    }

    /**
     * @api {POST} /v1/cp/members/status/ 03锁定/解锁用户
     * @apiName setMemberStatus
     * @apiGroup ADMIN_MEMBER
     * @apiParam {long} memberId 用户ID
     * @apiParam {int} status 1正常，2锁定
     * @apiSuccess (Success 200){int} code 200
     * @apiSuccess (Error 40001){int} code 40001 用户不存在
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> setMemberStatus(Http.Request request) {
        JsonNode requestNode = request.body().asJson();
        long memberId = requestNode.findPath("memberId").asLong();
        int status = requestNode.findPath("status").asInt();
        Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
        return CompletableFuture.supplyAsync(() -> {
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            if (memberId < 1) return okCustomJson(CODE40001, "参数错误");
            if (status != Member.MEMBER_STATUS_LOCK && status != Member.MEMBER_STATUS_NORMAL)
                return okCustomJson(CODE40001, "参数错误");
            Member member = Member.find.byId(memberId);
            if (null == member) return okCustomJson(CODE40001, "用户不存在");
            member.setStatus(status);
            member.save();
            //将用户的缓存清掉
            deleteMemberLoginStatus(member);
            businessUtils.addOperationLog(request, admin, "锁定/解锁用户，uid:" + member.id);
            return okJSON200();
        });
    }

    private void deleteMemberLoginStatus(Member member) {
        int[] deviceType = new int[]{HttpRequestDeviceUtils.TYPE_PC, HttpRequestDeviceUtils.TYPE_MOBILE, HttpRequestDeviceUtils.TYPE_APP};
        Arrays.stream(deviceType).forEach((each) -> {
            String tokenKey = cacheUtils.getMemberTokenKey(each, member.id);
            String key = cacheUtils.getMemberKey(each, tokenKey);
            Optional<String> tokenCache = syncCache.getOptional(tokenKey);
            if (tokenCache.isPresent()) {
                syncCache.remove(tokenCache.get());
            }
            syncCache.remove(tokenKey);
            syncCache.remove(key);
        });

    }


    /**
     * @api {GET} /v1/cp/member_balance_log/?page=&uid=&queryType= 04流水记录
     * @apiName listUserBalanceLog
     * @apiGroup ADMIN_MEMBER
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200) {JsonArray} list 列表
     * @apiSuccess (Success 200) {double} leftBalance 变动前的相应的用户余额
     * @apiSuccess (Success 200) {double} freezeBalance 变动前的相应的用户冻结余额
     * @apiSuccess (Success 200) {double} totalBalance 变动前的相应的用户总额
     * @apiSuccess (Success 200) {double} changeAmount 变动余额
     * @apiSuccess (Success 200) {int} transactionType  流水类型
     * @apiSuccess (Success 200) {int} moneyChangedType 变动金额类型，1是可用余额变动 2是冻结余额变动
     * @apiSuccess (Success 200) {String} description 变动理由
     * @apiSuccess (Success 200) {String} createdTime 创建时间
     */
    public CompletionStage<Result> listUserBalanceLog(int page, long uid, int queryType) {
        return CompletableFuture.supplyAsync(() -> {
            ExpressionList<BalanceLog> expressionList = BalanceLog.find.query().where()
                    .eq("itemId", CASH);
            if (uid > 0) expressionList.eq("uid", uid);
            if (queryType > 0) {
                if (queryType == BusinessConstant.QUERY_TYPE_CHARGE) {
                    expressionList.or(Expr.eq("bizType", TRANSACTION_TYPE_DEPOSIT),
                            Expr.eq("bizType", TRANSACTION_TYPE_GIVE_FOR_CHARGE));
                } else if (queryType == BusinessConstant.QUERY_TYPE_CONSUME)
                    expressionList.eq("bizType", TRANSACTION_TYPE_PLACE_ORDER);
            }
            PagedList<BalanceLog> pagedList = expressionList.orderBy().desc("id")
                    .setFirstRow((page - 1) * BusinessConstant.PAGE_SIZE_20)
                    .setMaxRows(BusinessConstant.PAGE_SIZE_20)
                    .orderBy().desc("id")
                    .findPagedList();
            int pages = pagedList.getTotalPageCount();
            List<BalanceLog> list = pagedList.getList();
            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            result.put("pages", pages);
            result.set("list", Json.toJson(list));
            return ok(result);
        });
    }

}
