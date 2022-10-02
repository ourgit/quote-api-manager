package utils;

import com.fasterxml.jackson.databind.node.ObjectNode;
import constants.BusinessConstant;
import constants.RedisKeyConstant;
import models.category.Category;
import models.admin.AdminMember;
import models.admin.Group;
import models.admin.GroupUser;
import models.log.SmsLog;
import models.post.PostCategory;
import models.product.NewShopCategory;
import models.shop.NewShopFactory;
import models.system.ParamConfig;
import models.user.Member;
import play.Logger;
import play.cache.NamedCache;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.mvc.Http;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import static constants.BusinessConstant.*;
import static constants.RedisKeyConstant.*;


@Singleton
public class BizUtils {

    Logger.ALogger logger = Logger.of(BizUtils.class);
    //TODO
    public static final int TOKEN_EXPIRE_TIME = 18000;

    @Inject
    CacheUtils cacheUtils;

    @Inject
    WSClient wsClient;

    @Inject
    EncodeUtils encodeUtils;

    @Inject
    IPUtil ipUtil;

    @Inject
    DateUtils dateUtils;

    @Inject
    @NamedCache("redis")
    protected play.cache.redis.AsyncCacheApi redis;

    public String getUIDFromRequest(Http.Request request) {
        Optional<String> authTokenHeaderValues = request.getHeaders().get(KEY_AUTH_TOKEN_UID);
        if (authTokenHeaderValues.isPresent()) {
            String authToken = authTokenHeaderValues.get();
            return authToken;
        }
        return "";
    }

    public long getCurrentTimeBySecond() {
        return System.currentTimeMillis() / 1000;
    }

    public int getTokenExpireTime() {
        return TOKEN_EXPIRE_TIME;
    }

    public boolean upToIPLimit(Http.Request request, String key, int max) {
        String ip = getRequestIP(request);
        if (!ValidationUtil.isEmpty(ip)) {
            Optional<String> optional = redis.sync().get(key + ip);
            String accessCount = "";
            if (optional.isPresent()) accessCount = optional.get();
            if (ValidationUtil.isEmpty(accessCount)) {
                redis.set(key + ip, "1", BusinessConstant.KEY_EXPIRE_TIME_2M);
            } else {
                int accessCountInt = Integer.parseInt(accessCount) + 1;
                if (accessCountInt > max) return true;
                redis.set(key + ip, String.valueOf(accessCountInt), BusinessConstant.KEY_EXPIRE_TIME_2M);
            }
        }
        return false;
    }

    public String getRequestIP(Http.Request request) {
        String ip = null;
        try {
            String remoteAddr = request.remoteAddress();
            String forwarded = request.getHeaders().get("X-Forwarded-For").get();
            String realIp = request.getHeaders().get(BusinessConstant.X_REAL_IP_HEADER).get();
            if (forwarded != null) {
                ip = forwarded.split(",")[0];
            }
            if (ValidationUtil.isEmpty(ip)) {
                ip = realIp;
            }
            if (ValidationUtil.isEmpty(ip)) {
                ip = remoteAddr;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return ip == null ? "" : escapeHtml(ip);
    }

    public boolean checkVcode(String accountName, String vcode) {
        if (ValidationUtil.isPhoneNumber(accountName)) {
            String key = cacheUtils.getSMSLastVerifyCodeKey(accountName);
            Optional<String> optional = redis.sync().get(key);
            if (optional.isPresent()) {
                String correctVcode = optional.get();
                if (!ValidationUtil.isEmpty(correctVcode)) {
                    if (ValidationUtil.isVcodeCorrect(vcode) && ValidationUtil.isVcodeCorrect(correctVcode) && vcode.equals(correctVcode))
                        return true;
                }
            }
        } else return false;
        return false;
    }

    public boolean uptoErrorLimit(Http.Request request, String key, int max) {
        Optional<String> optional = redis.sync().get(key);
        String accessCount = "";
        if (optional.isPresent()) {
            accessCount = optional.get();
        }
        if (ValidationUtil.isEmpty(accessCount)) {
            redis.set(key, "1", BusinessConstant.KEY_EXPIRE_TIME_2M);
        } else {
            int accessCountInt = Integer.parseInt(accessCount) + 1;
            if (accessCountInt > max) return true;
            redis.set(key, String.valueOf(accessCountInt), BusinessConstant.KEY_EXPIRE_TIME_2M);
        }
        return false;
    }

    public boolean isAdmin(AdminMember adminMember) {
        boolean isAdmin = false;
        if (null != adminMember) {
            List<GroupUser> groups = GroupUser.find.query().where().eq("memberId", adminMember.id).findList();
            for (GroupUser groupUser : groups) {
                Group group = Group.find.byId(groupUser.groupId);
                if (null != group) {
                    if (group.isAdmin) {
                        isAdmin = true;
                        break;
                    }
                }
            }
        }
        return isAdmin;
    }

    public void deleteVcodeCache(String accountName) {
        String key = cacheUtils.getSMSLastVerifyCodeKey(accountName);
        if (!ValidationUtil.isEmpty(key)) redis.remove(key);
    }

    /**
     * 转义html脚本
     *
     * @param value
     * @return
     */
    public String escapeHtml(String value) {
        if (ValidationUtil.isEmpty(value)) return "";
        value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        value = value.replaceAll("\\(", "（").replaceAll("\\)", "）");
        value = value.replaceAll("eval\\((.*)\\)", "");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        value = value.replaceAll("script", "");
        value = value.replaceAll("select", "");
        value = value.replaceAll("insert", "");
        value = value.replaceAll("update", "");
        value = value.replaceAll("delete", "");
        value = value.replaceAll("%", "\\%");
        value = value.replaceAll("union", "");
        value = value.replaceAll("load_file", "");
        value = value.replaceAll("outfile", "");
        return value;
    }

    public void addOperationLog(Http.Request request, AdminMember admin, String note) {
        String ip = getRequestIP(request);
        String place = "";
        if (!ValidationUtil.isEmpty(ip)) place = ipUtil.getCityByIp(ip);
        request.getHeaders()
                .adding("adminId", admin.id + "")
                .adding("adminName", admin.realName)
                .adding("ip", ip)
                .adding("place", place)
                .adding("note", note);
    }


    public Optional<AdminMember> getAdminByAuthToken(Http.Request request) {
        String uidToken = getUIDFromRequest(request);
        if (ValidationUtil.isEmpty(uidToken)) return Optional.empty();
        Optional<String> uidOptional = redis.sync().get(uidToken);
        if (!uidOptional.isPresent()) return Optional.empty();
        String uid = uidOptional.get();
        if (ValidationUtil.isEmpty(uid)) return Optional.empty();
        String key = ADMIN_KEY_MEMBER_ID_AUTH_TOKEN_PREFIX + uid;
        Optional<String> optional = redis.sync().get(key);
        if (!optional.isPresent()) return Optional.empty();
        String token = optional.get();
        if (ValidationUtil.isEmpty(token)) return Optional.empty();
        Optional<AdminMember> adminMemberOptional = redis.sync().get(token);
        if (!adminMemberOptional.isPresent()) return Optional.empty();
        AdminMember adminMember = adminMemberOptional.get();
        if (null == adminMember) return Optional.empty();
        return Optional.of(adminMember);
    }

    public List<Category> convertCategory(List<Category> postCategoryList) {
        List<Category> nodeList = new ArrayList<>();
        if (null == postCategoryList) return nodeList;
        for (Category node1 : postCategoryList) {
            boolean mark = false;
            for (Category node2 : postCategoryList) {
                if (node1.parentId == node2.id) {
                    mark = true;
                    if (node2.children == null)
                        node2.children = new ArrayList<>();
                    node2.children.add(node1);
                    break;
                }
            }
            if (!mark) {
                nodeList.add(node1);
            }
        }
        return nodeList;
    }

    public List<PostCategory> convertPostCategoryList(List<PostCategory> postCategoryList) {
        List<PostCategory> nodeList = new ArrayList<>();
        if (null == postCategoryList) return nodeList;
        for (PostCategory node1 : postCategoryList) {
            boolean mark = false;
            for (PostCategory node2 : postCategoryList) {
                if (node1.parentId == node2.id) {
                    mark = true;
                    if (node2.children == null)
                        node2.children = new ArrayList<>();
                    node2.children.add(node1);
                    break;
                }
            }
            if (!mark) {
                nodeList.add(node1);
            }
        }
        return nodeList;
    }

    public List<NewShopCategory> convertListToTreeNode2(List<NewShopCategory> categoryList) {
        List<NewShopCategory> nodeList = new ArrayList<>();
        if (null == categoryList) return nodeList;
        for (NewShopCategory node1 : categoryList) {
            boolean mark = false;
            for (NewShopCategory node2 : categoryList) {
                if (node1.parentId == node2.id) {
                    mark = true;
                    if (node2.children == null)
                        node2.children = new ArrayList<>();
                    node2.children.add(node1);
                    break;
                }
            }
            if (!mark) {
                nodeList.add(node1);
            }
        }
        return nodeList;
    }

    public List<NewShopFactory> convertFactoryListToTreeNode(List<NewShopFactory> categoryList) {
        List<NewShopFactory> nodeList = new ArrayList<>();
        if (null == categoryList) return nodeList;
        for (NewShopFactory node1 : categoryList) {
            boolean mark = false;
            for (NewShopFactory node2 : categoryList) {
                if (node1.parentId == node2.id) {
                    mark = true;
                    if (node2.children == null)
                        node2.children = new ArrayList<>();
                    node2.children.add(node1);
                    break;
                }
            }
            if (!mark) {
                nodeList.add(node1);
            }
        }
        return nodeList;
    }

    public boolean setLock(String id, String operationType) {
        String key = operationType + ":" + id;
        try {
            if (redis.exists(key).toCompletableFuture().get(10, TimeUnit.SECONDS)) return false;
            return redis.setIfNotExists(key, 1, 5).toCompletableFuture().get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("getLock:" + e.getMessage());
            redis.remove(key);
        }
        return true;
    }

    public void unLock(String uid, String operationType) {
        redis.remove(operationType + ":" + uid);
    }


    public void deleteArticleCache(String cateName, long articleId) {
        redis.remove(RedisKeyConstant.KEY_HOME_PAGE_BOTTOM_INFO_LINKS);
        redis.remove(RedisKeyConstant.KEY_HOME_PAGE_INFO_LINKS);
        if (!ValidationUtil.isEmpty(cateName)) {
            String key = ARTICLE_LIST_JSON_CACHE + cateName;
            redis.remove(key);
        }
        if (articleId > 0) {
            String articleJsonCache = ARTICLE_JSON_CACHE + articleId;
            redis.remove(articleJsonCache);
        }
        String recommendJsonCache = ARTICLE_RECOMMEND_JSON_CACHE;
        redis.remove(recommendJsonCache);
    }


    public Member getMember(long uid) {
        Member member = null;
        String key = cacheUtils.getMemberKey(uid);
        Optional<Member> optional = redis.sync().get(key);
        if (optional.isPresent()) {
            member = optional.get();
        }
        if (null == member) {
            member = Member.find.query().select("id,nickName,avatar,realName")
                    .where().eq("id", uid).setMaxRows(1).findOne();
            if (null != member) {
                redis.set(key, member, 30 * 24 * 3600);
            }
        }
        return member;
    }

    public String generateVerificationCode() {
        Random ran = new Random();
        String code = String.valueOf(100000 + ran.nextInt(900000));
        return code;
    }

    public CompletionStage<ObjectNode> sendSingleSMS(String phoneNumber, String vcode, String content) {
        String appKey = getConfigValue(PARAM_KEY_SMS_NOTIFY_APP_KEY);
        String apiSecret = getConfigValue(PARAM_KEY_SMS_NOTIFY_API_SECRET);
        return sendSMS(phoneNumber, vcode, content, appKey, apiSecret);
    }

    public CompletionStage<ObjectNode> sendMultiSMS(String phoneNumber, String content) {
        String appKey = getConfigValue(PARAM_KEY_SMS_BUSINESS_APP_KEY);
        String apiSecret = getConfigValue(PARAM_KEY_SMS_BUSINESS_API_SECRET);
        return sendSMS(phoneNumber, "", content, appKey, apiSecret);
    }

    public CompletionStage<ObjectNode> sendSMS(String phoneNumber, String vcode, String content, String appKey, String apiSecret) {
        return CompletableFuture.supplyAsync(() -> {
            ObjectNode node = Json.newObject();
            node.put("code", 200);
            String requestUrl = getConfigValue(PARAM_KEY_SMS_REQUEST_URL);
            if (ValidationUtil.isEmpty(requestUrl)) {
                node.put("code", 500);
                node.put("reason", "短信请求地址为空");
                logger.info(node.findPath("reason").asText());
                return node;
            }
            if (ValidationUtil.isEmpty(appKey)) {
                node.put("code", 500);
                node.put("reason", "appKey为空");
                logger.info(node.findPath("reason").asText());
                return node;
            }
            if (ValidationUtil.isEmpty(apiSecret)) {
                node.put("code", 500);
                node.put("reason", "apiSecret为空");
                logger.info(node.findPath("reason").asText());
                return node;
            }
            String param = "appkey=" + appKey + "&appsecret=" + apiSecret + "&mobile=" + phoneNumber +
                    "&content=" + content;
            SmsLog smsLog = new SmsLog();
            smsLog.setPhoneNumber(phoneNumber);
            smsLog.setContent(requestUrl + "" + param);
            long unixStamp = System.currentTimeMillis();
            smsLog.setExtno(unixStamp + "");
            smsLog.setReqStatus("");
            smsLog.setRespStatus("");
            smsLog.setReqTime(unixStamp / 1000);
            smsLog.save();
            return wsClient.url(requestUrl).setContentType("application/x-www-form-urlencoded")
                    .post(param).thenApplyAsync((response) -> {
                        ObjectNode returnNode = Json.newObject();
                        ObjectNode result = (ObjectNode) Json.parse(response.getBody());
                        if (!ValidationUtil.isEmpty(vcode)) {
                            String key = cacheUtils.getSMSLastVerifyCodeKey(phoneNumber);
                            redis.set(key, vcode, 10 * 60);
                            if (null != result) {
                                String resultCode = result.findPath("code").asText();
                                if (resultCode.equalsIgnoreCase("0")) {
                                    returnNode.put("code", 200);
                                    //设置缓存，用于判断一分钟内请求短信多少
                                    String existRequestKey = EXIST_REQUEST_SMS + phoneNumber;
                                    redis.set(existRequestKey, existRequestKey, 60);
                                } else {
                                    logger.info(response.getBody());
                                }
                                smsLog.setMsgId(result.findPath("smsid").asText());
                                smsLog.setReqStatus(resultCode);
                            }
                        }
                        smsLog.setRespTime(System.currentTimeMillis() / 1000);
                        smsLog.save();
                        if (null != result) returnNode.set("result", result);
                        return returnNode;
                    }).toCompletableFuture().join();
        });
    }


    public String getConfigValue(String key) {
        String value = "";
        Optional<Object> accountOptional = redis.sync().get(key);
        if (accountOptional.isPresent()) {
            value = (String) accountOptional.get();
            if (!ValidationUtil.isEmpty(value)) return value;
        }
        if (ValidationUtil.isEmpty(value)) {
            ParamConfig config = ParamConfig.find.query().where()
                    .eq("key", key)
                    .orderBy().asc("id")
                    .setMaxRows(1).findOne();
            if (null != config) {
                value = config.value;
                redis.set(key, value, 30 * 3600 * 24);
            }
        }
        return value;
    }


    public String makeInviteCode() {
        String dealerCode = SerialCodeGenerator.makeSerialNumberOnlyDigital();
        Member promotion = Member.find.query().where().eq("dealerCode", dealerCode).findOne();
        while (null != promotion) {
            dealerCode = makeInviteCode();
        }
        return dealerCode;
    }

    public boolean checkPassword(String password) {
        if (null == password || password.length() < 6 || password.length() > 20) return false;
        else return true;
    }

    public String getMemberName(Member member) {
        String name = "";
        if (null != member) {
            name = member.realName;
            if (ValidationUtil.isEmpty(name)) name = member.nickName;
        }
        return name;
    }

    public int getSelfTakenPlaceAwardPercentage() {
        int directAwardPercentage = 3;
        String value = getConfigValue(PARAM_KEY_AWARD_SELF_TAKEN_PLACE_PERCENTAGE);
        if (!ValidationUtil.isEmpty(value)) {
            directAwardPercentage = Integer.parseInt(value);
        }
        return directAwardPercentage;
    }

    public String convertScene(ObjectNode node) {
        String temp = Json.stringify(node);
        return temp.replaceAll("\\{", "(")
                .replaceAll("\\}", ")")
                .replaceAll("\"", "'");
    }


    public String getAccessToken() {
        String token = "";
        Optional<Object> optional = redis.sync().get(MINI_APP_ACCESS_TOKEN);
        if (optional.isPresent()) {
            token = (String) optional.get();
        }
        return token;
    }


    public String limit20(String value) {
        if (ValidationUtil.isEmpty(value)) return "";
        if (value.length() > 20) return value.substring(0, 17) + "...";
        return value;
    }

    public String limit(String value, int limit) {
        if (ValidationUtil.isEmpty(value)) return "";
        if (value.length() > limit) return value.substring(0, limit - 3) + "...";
        return value;
    }


    public void removeTabProductCache(long eachTabId) {
        for (int i = 1; i < 100; i++) {
            String key = cacheUtils.getProductTabDetail(eachTabId, i);
            redis.remove(key);
        }
    }


    public String getDomain() {
        return getConfigValue(PARAM_KEY_DEFAULT_HOME_PAGE_URL);
    }

    public String getWechatMpAppId() {
        return getEncryptConfigValue(PARAM_KEY_WECHAT_MP_APP_ID);
    }

    public String getWechatMpSecretCode() {
        return getEncryptConfigValue(PARAM_KEY_WECHAT_MP_SECRET_CODE);
    }

    public String getWechatMiniAppId() {
        return getEncryptConfigValue(PARAM_KEY_WECHAT_MINI_APP_ID);
    }

    public String getWechatMiniAppSecretCode() {
        return getEncryptConfigValue(PARAM_KEY_WECHAT_MINI_APP_SECRET_CODE);
    }

    public String getWechatMchId() {
        return getEncryptConfigValue(PARAM_KEY_WECHAT_MCH_ID);
    }

    public String getWechatMchAppSecretCode() {
        return getEncryptConfigValue(PARAM_KEY_WECHAT_MCH_API_SECURITY_CODE);
    }

    public String getEncryptConfigValue(String key) {
        Optional<Object> accountOptional = redis.sync().get(key);
        if (accountOptional.isPresent()) {
            String value = (String) accountOptional.get();
            if (!ValidationUtil.isEmpty(value)) return value;
        }
        ParamConfig config = ParamConfig.find.query().where()
                .eq("key", key)
                .orderBy().asc("id")
                .setMaxRows(1).findOne();
        if (null != config && !ValidationUtil.isEmpty(config.value)) {
            String decryptValue = encodeUtils.decrypt(config.value);
            redis.set(key, decryptValue);
        }
        return "";
    }

    public String getAlinYunAccessId() {
        return getEncryptConfigValue(PARAM_KEY_ALI_YUN_ACCESS_ID);
    }

    public String getAliYunSecretKey() {
        return getEncryptConfigValue(PARAM_KEY_ALI_YUN_SECRET_KEY);
    }

    public String getAlipayAppId() {
        return getConfigValue(PARAM_KEY_ALIPAY_APPID);
    }

    public String getAlipayAppPrivateKey() {
        return getConfigValue(PARAM_KEY_ALIPAY_APP_PRIVATE_KEY);
    }

    public String getAlipayAliPublicKeyRSA2() {
        return getConfigValue(PARAM_KEY_ALIPAY_ALI_PUBLIC_KEY_RSA2);
    }

    public String getAlipayAliPublicKey() {
        return getConfigValue(PARAM_KEY_ALIPAY_ALI_PUBLIC_KEY);
    }

    public String getAlipayWapPayNotifyUrl() {
        return getConfigValue(PARAM_KEY_ALIPAY_WAP_PAY_NOTIFY_URL);
    }

    public String getAlipayDirectPayNotifyUrl() {
        return getConfigValue(PARAM_KEY_ALIPAY_DIRECT_PAY_NOTIFY_URL);
    }

    public String getAlipayReturnUrl() {
        return getConfigValue(PARAM_KEY_ALIPAY_RETURN_URL);
    }

    public String getAlipayPartnerNo() {
        return getConfigValue(PARAM_KEY_ALIPAY_PARTNER_NO);
    }

    public String getKDHostUrl() {
        return getConfigValue(PARAM_KEY_KD_HOST_URL);
    }

    public String getKDAppCode() {
        return getConfigValue(PARAM_KEY_KD_APP_CODE);
    }
}

