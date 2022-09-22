package controllers.basic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.BaseAdminSecurityController;
import io.ebean.DB;
import models.admin.AdminMember;
import models.admin.Group;
import models.admin.GroupUser;
import models.shop.Shop;
import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import utils.EncodeUtils;
import utils.Pinyin4j;
import utils.ValidationUtil;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static constants.RedisKeyConstant.KEY_LOGIN_MAX_ERROR_TIMES;

/**
 * 成员管理器
 */
public class AdminMemberController extends BaseAdminSecurityController {
    @Inject
    EncodeUtils encodeUtils;

    @Inject
    Pinyin4j pinyin4j;

    /**
     * @api {GET} /v1/cp/admin_members/:memberId/ 01查看管理员详情
     * @apiName getAdminMember
     * @apiGroup Admin-Member
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess {int} id 用户id
     * @apiSuccess {string} userName 用户名
     * @apiSuccess {string} realName 真名
     * @apiSuccess {double} awardPercentage 返点点数
     * @apiSuccess {String} createdTimeForShow 登录时间
     * @apiSuccess {String} lastLoginIP 登录ip
     * @apiSuccess {int} groupId 所在分组id
     * @apiSuccess {long} shopId 机构ID
     * @apiSuccess {String} avatar 头像
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     * @apiSuccess (Error 40002) {int} code 40002 该管理员不存在
     */
    public CompletionStage<Result> getAdminMember(long memberId) {
        return CompletableFuture.supplyAsync(() -> {
            if (memberId < 1) return okCustomJson(CODE40001, "参数错误");
            AdminMember member = AdminMember.find.byId(memberId);
            if (null == member) return okCustomJson(CODE40002, "该成员不存在");
            ObjectNode result = (ObjectNode) Json.toJson(member);
            List<GroupUser> groupUser = GroupUser.find.query().where().eq("memberId", member.id)
                    .findList();
            result.set("groups", Json.toJson(groupUser));
            result.put("code", 200);
            return ok(result);
        });
    }

    /**
     * @api {GET} /v1/cp/admin_members/ 02管理员列表
     * @apiName listAdminMembers
     * @apiGroup Admin-Member
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess {json} list
     * @apiSuccess {int} id 用户id
     * @apiSuccess {string} userName 用户名
     * @apiSuccess {string} realName 真名
     * @apiSuccess {String} groupName 岗位名
     * @apiSuccess {String} orgName 部门
     * @apiSuccess {double} awardPercentage 返点点数
     * @apiSuccess {int} groupId 组id
     * @apiSuccess {String} createdTime 登录时间
     * @apiSuccess {String} lastLoginIP 登录ip
     */
    public CompletionStage<Result> listAdminMembers() {
        return CompletableFuture.supplyAsync(() -> {
            List<AdminMember> list = AdminMember.find.query().where().orderBy().asc("id").findList();
            list.parallelStream().forEach((each) -> {
                List<GroupUser> groupUserList = GroupUser.find.query().where().eq("memberId", each.id)
                        .orderBy().asc("id")
                        .findList();
                each.groupUserList.addAll(groupUserList);
                if (each.shopId > 0 && ValidationUtil.isEmpty(each.shopName)) {
                    Shop shop = Shop.find.byId(each.shopId);
                    if (null != shop) {
                        each.setShopName(shop.name);
                        each.save();
                    }
                }
            });

            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            result.set("list", Json.toJson(list));
            return ok(result);
        });

    }


    /**
     * @api {POST} /v1/cp/admin_member/new/ 03添加成员
     * @apiName addAdminMember
     * @apiGroup Admin-Member
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiParam {string} userName 用户名
     * @apiParam {string} realName 真名
     * @apiParam {string} password 密码6-20
     * @apiParam {string} [avatar] 头像地址
     * @apiParam {double} awardPercentage 返点点数
     * @apiParam {JsonArray} [groupIdList] groupId的数组
     * @apiParam {long} shopId 部门ID
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     * @apiSuccess (Error 40002) {int} code 40002 该管理员已存在
     * @apiSuccess (Error 40003) {int} code 40003 分组不存在
     * @apiSuccess (Error 40004) {int} code 40004 该用户已在分组成员中
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> addAdminMember(Http.Request request) {
        JsonNode node = request.body().asJson();
        String ip = request.remoteAddress();
        Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
        return CompletableFuture.supplyAsync(() -> {
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            AdminMember member = Json.fromJson(node, AdminMember.class);
            String password = node.findPath("password").asText();
            member.setPassword(password);
            if (ValidationUtil.isEmpty(member.realName)) return okCustomJson(CODE40001, "请输入成员名字");
            if (!ValidationUtil.isValidPassword(member.password)) return okCustomJson(CODE40001, "密码6-20位");
            if (ValidationUtil.isEmpty(member.userName)) return okCustomJson(CODE40001, "请输入帐号");
            if (member.userName.length() > 20) return okCustomJson(CODE40001, "帐号最长20位");
            List<AdminMember> existMembers = AdminMember.find.query().where().eq("userName", member.userName).findList();
            if (existMembers.size() > 0) return okCustomJson(CODE40002, "该用户已存在");
            long currentTime = dateUtils.getCurrentTimeBySecond();
            long shopId = node.findPath("shopId").asLong();
            if (shopId > 0) {
                Shop org = Shop.find.byId(shopId);
                if (null == org) return okCustomJson(CODE40001, "该部门不存在");
                member.setShopId(shopId);
                member.setShopName(org.name);
            }
            String avatar = node.findPath("avatar").asText();
            if (!ValidationUtil.isEmpty(avatar)) member.setAvatar(avatar);
            String phoneNumber = node.findPath("phoneNumber").asText();
            if (!ValidationUtil.isEmpty(phoneNumber)) {
                if (!ValidationUtil.isPhoneNumber(phoneNumber)) return okCustomJson(CODE40001, "手机号码有误");
                member.setPhoneNumber(phoneNumber);
            }
            member.setPassword(encodeUtils.getMd5WithSalt(member.password));
            member.setCreatedTime(currentTime);
            member.setLastLoginIP(ip);
            member.setLastLoginTime(currentTime);
            member.setPinyinAbbr(pinyin4j.toPinYinUppercase(member.realName));
            member.save();
            saveGroup(node, member);
            businessUtils.addOperationLog(request, admin, "添加成员：" + member.toString());
            return okJSON200();
        });

    }

    /**
     * @api {POST} /v1/cp/admin_member/:id/ 04修改管理员
     * @apiName updateAdminMember
     * @apiGroup Admin-Member
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiParam {string} [userName] 用户名
     * @apiParam {string} [realName] 真名
     * @apiParam {string} [password] 新密码6-20
     * @apiParam {string} [avatar] 头像地址
     * @apiParam {double} [awardPercentage] 返点点数
     * @apiParam {JsonArray} [groupIdList] groupId的数组
     * @apiParam {long} [shopId] 部门ID
     * @apiSuccess (Success 40001) {int} code 40001 参数错误
     * @apiSuccess (Success 40001) {int} code 40002 该管理员不存在
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> updateAdminMember(Http.Request request, long id) {
        JsonNode node = request.body().asJson();
        Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
        return CompletableFuture.supplyAsync(() -> {
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            AdminMember updateMember = Json.fromJson(node, AdminMember.class);
            if (null == updateMember) return okCustomJson(CODE40001, "参数错误");
            AdminMember existMember = AdminMember.find.query().where().eq("id", id).findOne();
            if (null == existMember) return okCustomJson(CODE40002, "该成员不存在");
            businessUtils.addOperationLog(request, admin, "修改权限，执行前" + existMember.toString() + ";修改的参数：" + updateMember.toString());


            if (!ValidationUtil.isEmpty(updateMember.userName) && !updateMember.userName.equals(existMember.userName))
                existMember.setUserName(updateMember.userName);
            if (!ValidationUtil.isEmpty(updateMember.realName) && !updateMember.realName.equals(existMember.realName)) {
                existMember.setRealName(updateMember.realName);
                List<GroupUser> groupUser = GroupUser.find.query().where().eq("memberId", existMember.id).findList();
                groupUser.parallelStream().forEach((each) -> {
                    each.setRealName(updateMember.realName);
                    each.save();
                });
                existMember.setPinyinAbbr(pinyin4j.toPinYinUppercase(existMember.realName));
            }
            if (node.has("shopId")) {
                long shopId = node.findPath("shopId").asLong();
                if (shopId > 0) {
                    Shop org = Shop.find.byId(shopId);
                    if (null == org) return okCustomJson(CODE40001, "该部门不存在");
                    existMember.setShopId(shopId);
                    existMember.setShopName(org.name);
                } else {
                    existMember.setShopId(0);
                    existMember.setShopName("");
                }
            }

            String avatar = node.findPath("avatar").asText();
            if (!ValidationUtil.isEmpty(avatar)) {
                existMember.setAvatar(avatar);
            }
            String phoneNumber = node.findPath("phoneNumber").asText();
            if (!ValidationUtil.isEmpty(phoneNumber)) {
                if (!ValidationUtil.isPhoneNumber(phoneNumber)) return okCustomJson(CODE40001, "手机号码有误");
                existMember.setPhoneNumber(phoneNumber);
            }
            if (node.has("awardPercentage")) {
                double awardPercentage = node.findPath("awardPercentage").asDouble();
                existMember.setAwardPercentage(awardPercentage);
            }
            int status = node.findPath("status").asInt();
            if (status > 0) {
                existMember.setStatus(status);
            }
            existMember.save();
            saveGroup(node, existMember);
            return okJSON200();
        });
    }

    private void saveGroup(JsonNode node, AdminMember existMember) {
        if (node.has("groupIdList")) {
            ArrayNode list = (ArrayNode) node.findPath("groupIdList");
            if (null != list && list.size() > 0) {
                List<GroupUser> groupUsers = new ArrayList<>();
                if (list.size() > 0) {
                    long currentTime = dateUtils.getCurrentTimeBySecond();
                    list.forEach((each) -> {
                        GroupUser groupUser = new GroupUser();
                        Group group = Group.find.byId(each.asInt());
                        if (null != group) {
                            groupUser.setGroupId(group.id);
                            groupUser.setGroupName(group.groupName);
                            groupUser.setMemberId(existMember.id);
                            groupUser.setRealName(existMember.realName);
                            groupUser.setCreateTime(currentTime);
                            groupUsers.add(groupUser);
                        }
                    });
                    if (list.size() > 0) {
                        //删除旧的
                        List<GroupUser> oldGroupUser = GroupUser.find.query().where()
                                .eq("memberId", existMember.id).findList();
                        if (oldGroupUser.size() > 0) DB.deleteAll(oldGroupUser);
                        DB.saveAll(groupUsers);
                    }
                }
            }
        }
    }


    /**
     * @api {POST} /v1/cp/admin_member/ 05删除管理员
     * @apiName delAdminMember
     * @apiGroup Admin-Member
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiParam {int} id 管理员id
     * @apiParam {String} operation 操作,"del"为删除
     * @apiSuccess (Success 40001) {int} code 40001 参数错误
     * @apiSuccess (Success 40002) {int} code 40002 该管理员不存在
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> delAdminMember(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
        return CompletableFuture.supplyAsync(() -> {
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            String operation = jsonNode.findPath("operation").asText();
            if (ValidationUtil.isEmpty(operation) || !operation.equals("del")) return okCustomJson(CODE40001, "参数错误");
            long id = jsonNode.findPath("id").asLong();
            if (id < 1) return okCustomJson(CODE40001, "参数错误");
            AdminMember member = AdminMember.find.byId(id);
            if (null == member) return okCustomJson(CODE40002, "该成员不存在");
            businessUtils.addOperationLog(request, admin, "删除成员：" + member.toString());
            member.delete();
            List<GroupUser> list = GroupUser.find.query().where().eq("memberId", id).findList();
            if (list.size() > 0) DB.deleteAll(list);
            return okJSON200();
        });
    }


    /**
     * @api {POST} /v1/cp/admin_members/status/ 07锁定/解锁管理员
     * @apiName lockMember
     * @apiGroup Admin-Member
     * @apiParam {long} memberId 用户ID
     * @apiParam {int} status 1正常，2锁定
     * @apiSuccess (Success 200){int} code 200
     * @apiSuccess (Error 40001){int} code 40001 用户不存在
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> setAdminMemberStatus(Http.Request request) {
        JsonNode requestNode = request.body().asJson();
        long memberId = requestNode.findPath("memberId").asLong();
        int status = requestNode.findPath("status").asInt();
        Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
        return CompletableFuture.supplyAsync(() -> {
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            if (memberId < 1) return okCustomJson(CODE40001, "参数错误");
            if (status != AdminMember.STATUS_NORMAL && status != AdminMember.STATUS_LOCK)
                return okCustomJson(CODE40001, "参数错误");
            AdminMember member = AdminMember.find.byId(memberId);
            if (null == member) return okCustomJson(CODE40001, "该用户不存在");
            member.setStatus(status);
            if (status == AdminMember.STATUS_NORMAL) syncCache.remove(KEY_LOGIN_MAX_ERROR_TIMES + member.id);
            member.save();
            businessUtils.addOperationLog(request, admin, "锁定/解锁成员：" + member.toString());
            return okJSON200();
        });
    }

    /**
     * @api {GET} /v1/cp/admin_member/info/ 08查看自己详情信息
     * @apiName getAdminMemberInfo
     * @apiGroup Admin-Member
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess {int} id 用户id
     * @apiSuccess {string} name 用户名
     * @apiSuccess {string} avatar avatar
     * @apiSuccess {int} groupId 所在分组id
     * @apiSuccess {long} shopId 机构ID
     * @apiSuccess {String} avatar 头像
     * @apiSuccess (Error 40001) {int} code 40001 参数错误
     * @apiSuccess (Error 40002) {int} code 40002 该管理员不存在
     */
    public CompletionStage<Result> getAdminMemberInfo(Http.Request request) {
        Optional<AdminMember> optional = businessUtils.getAdminByAuthToken(request);
        return CompletableFuture.supplyAsync(() -> {
            if (!optional.isPresent()) return unauth403();
            AdminMember admin = optional.get();
            if (null == admin) return unauth403();
            AdminMember member = AdminMember.find.byId(admin.id);
            if (null == member) return unauth403();
            ObjectNode result = (ObjectNode) Json.toJson(member);
            result.put("code", 200);
            result.put("id", member.id);
            result.put("name", member.realName);
            result.put("avatar", member.avatar);
            result.put("shopName", member.shopName);
            result.put("shopId", member.shopId);
            result.put("introduction", "");

            List<Integer> groupIdList = new ArrayList<>();
            List<GroupUser> groupUserList = GroupUser.find.query().where()
                    .eq("memberId", admin.id)
                    .orderBy().asc("id")
                    .findList();
            List<String> roleList = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            groupUserList.parallelStream().forEach((each) -> {
                        roleList.add(each.groupName);
                        sb.append(each.groupName).append(" ");
                        groupIdList.add(each.groupId);
                    }
            );
            boolean isAdmin = false;
            for (GroupUser groupUser : groupUserList) {
                Group group = Group.find.byId(groupUser.groupId);
                if (null != group) {
                    isAdmin = group.isAdmin;
                    if (isAdmin) break;
                }
            }
            result.put("isAdmin", isAdmin);
            result.put("groupName", sb.toString());
            result.set("roles", Json.toJson(roleList));
            result.set("groupIdList", Json.toJson(groupIdList));
            result.put("lastLoginTime", dateUtils.formatToYMDHMSBySecond(member.lastLoginTime));
            result.put("createdTime", dateUtils.formatToYMDHMSBySecond(member.createdTime));
            return ok(result);
        });
    }

    /**
     * @api {POST} /v1/cp/bind_member_to_group/ 09批量绑定用户到角色组
     * @apiName bindMemberToGroup
     * @apiGroup Admin-Member
     * @apiParam {long} uid 用户ID
     * @apiParam {JsonArray} list groupId的数组
     * @apiSuccess (Success 200){int} code 200
     */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public CompletionStage<Result> bindMemberToGroup(Http.Request request) {
        JsonNode jsonNode = request.body().asJson();
        return CompletableFuture.supplyAsync(() -> {
            if (null == jsonNode) return okCustomJson(CODE40001, "参数错误");
            Optional<AdminMember> adminOptional = businessUtils.getAdminByAuthToken(request);
            if (!adminOptional.isPresent()) return unauth403();
            AdminMember admin = adminOptional.get();
            long uid = jsonNode.findPath("uid").asLong();
            AdminMember member = AdminMember.find.byId(uid);
            if (null == member) return okCustomJson(CODE40001, "用户ID有误");

            ArrayNode list = (ArrayNode) jsonNode.findPath("list");
            if (null != list && list.size() > 0) {
                List<GroupUser> groupUsers = new ArrayList<>();
                if (list.size() > 0) {
                    long currentTime = dateUtils.getCurrentTimeBySecond();
                    list.forEach((node) -> {
                        GroupUser groupUser = new GroupUser();
                        Group group = Group.find.byId(node.asInt());
                        if (null != group) {
                            groupUser.setGroupId(group.id);
                            groupUser.setGroupName(group.groupName);
                            groupUser.setMemberId(uid);
                            groupUser.setRealName(member.realName);
                            groupUser.setCreateTime(currentTime);
                            groupUsers.add(groupUser);
                        }
                    });
                    if (list.size() > 0) {
                        //删除旧的
                        List<GroupUser> oldGroupUser = GroupUser.find.query().where()
                                .eq("memberId", member.id).findList();
                        if (oldGroupUser.size() > 0) DB.deleteAll(oldGroupUser);
                        DB.saveAll(groupUsers);
                    }
                }
            }
            businessUtils.addOperationLog(request, admin, "批量修改用户角色：" + jsonNode.toString());
            return okJSON200();
        });
    }

    /**
     * @api {GET} /v1/cp/user_groups/?memberId= 10用户所属分组
     * @apiName listUserGroups
     * @apiGroup Admin-Member
     * @apiSuccess {json} list
     * @apiSuccess (Success 200) {int} code 200 请求成功
     */
    public CompletionStage<Result> listUserGroups(Http.Request request, long memberId) {
        return CompletableFuture.supplyAsync(() -> {
            List<GroupUser> list = GroupUser.find.query().where().eq("memberId", memberId).findList();
            ObjectNode node = Json.newObject();
            node.put("code", 200);
            node.set("list", Json.toJson(list));
            return ok(node);
        });
    }

}
