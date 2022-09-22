package constants;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 业务用的常量
 * Created by win7 on 2016/6/11.
 */
public class BusinessConstant {

    //    public static final String DEFAULT_HOME_PAGE_URL = "https://qd.starnew.cn";
    public static final String DEFAULT_AVATAR_URL = "https://img.maiyacang.com/avatar.png";

    public static final int KEY_EXPIRE_TIME_2M = 120;

    public static final int HOUR24_TO_SECONDS = 86400;

    public static final String KEY_SESSION_WECHAT_OPEN_ID = "xx_play";

    public static final int TYPE_PLACE_ORDER = 1;
    public static final int TYPE_CANCEL_ORDER = 2;

    public static final int DEVICE_TYPE_WECHAT = 5;

    public static final int MAX_TIME_TO_WAIT_PAY = 60 * 60;//下单后1小时内没支付自动关闭订单

    /**
     * 支付方式,在线支付(网银)
     */
    public static final int PAYMENT_BANK = 1;

    /**
     * 支付方式,支付宝
     */
    public static final int PAYMENT_ALIPAY = 2;
    /**
     * 支付方式,微信支付
     */
    public static final int PAYMENT_WEPAY = 3;

    /**
     * 支付方式,余额支付
     */
    public static final int PAYMENT_BALANCE_PAY = 4;
    /**
     * 支付方式,现金，该类型用于充值
     */
    public static final int PAYMENT_CASH = 5;

    /**
     * 支付方式,微信支付，H5调用微信APP支付
     */
    public static final int PAYMENT_WEPAY_H5 = 6;

    /**
     * 支付方式,支付宝H5支付
     */
    public static final int PAYMENT_ALIPAY_WAP = 7;

    /**
     * 支付方式,微信支付，小程序调用微信支付
     */
    public static final int PAYMENT_WEPAY_MINIAPP = 8;

    /**
     * 支付方式,11定金支付
     */
    public static final int PAYMENT_PRE_PAY = 11;

    public static final BigDecimal BG100 = new BigDecimal("100.00");
    public static final BigDecimal BG1000 = new BigDecimal("1000.00");
    public static final BigDecimal BG10000 = new BigDecimal("10000.00");
    public static final BigDecimal TORLANCE = new BigDecimal("0.0001");
    public static final DecimalFormat DF_TWO_DIGIT = new DecimalFormat("######0.00");
    public static final String OPERATION_CONFIRMATION = "confirmation";
    public static final String OPERATION_CANCEL = "cancel";
    public static final String OPERATION_UPDATE = "update";
    public static final String X_REAL_IP_HEADER = "X-real-ip";

    public static final int MAX_SMS_ACCESS_PER_IP = 10;
    public static final int MAX_SECURE_CODE_ACCESS_PER_IP = 10;
    public static final int MAX_REG_ACCESS_PER_IP = 2;

    public static final int TYPE_LOGIN = 1;//正常登录
    public static final int TYPE_UNUSUAL_LOGIN = 2;//异地登录
    public static final int TYPE_PRMOTION = 3;//营销短信
    public static final int TYPE_CHARGE_RESULT = 4;//充值结果
    public static final int TYPE_CONSUME_NOTIFY = 5;//消费提醒
    public static final int TYPE_CHARGE_PROMPT = 6;//充值提醒
    public static final String SMS_REQUEST_URL = "http://pin.shujudu.cn/api/sms/send/single/?key=%s&secret=%s&mobile=%s&msgid=%s&content=%s";
    public static final String APP_KEY = "7ec9eecab6";
    public static final String APP_SECRET = "5954a8be024640e7c48322b4d417758f";

    public static final int GROUPON_REQUIRE_ORDERS = 2;
    public static final int GROUPON_TIME_LIMIT_SECONDS = 1800;

    /**
     * 管理员手动充值
     */
    public static final String OPERATION_ADMIN_MANNUALLY_DEPOSIT = "OPERATION_ADMIN_MANNUALLY_DEPOSIT";


    /**
     * 下单
     */
    public static final String OPERATION_PLACE_ORDER = "OPERATION_PLACE_ORDER";
    public static final String OPERATION_TRANSFER_TO_BALANCE = "OPERATION_TRANSFER_TO_BALANCE";
    public static final String OPERATION_AUDIT_STORE = "OPERATION_AUDIT_STORE";
    public static final String OPERATION_SET_USER_AUTH = "OPERATION_SET_USER_AUTH";
    public static final String OPERATION_AUTH_SHOP = "OPERATION_AUTH_SHOP";
    /**
     * 支付订单
     */
    public static final String OPERATION_PAY_ORDER = "OPERATION_PAY_ORDER";
    /**
     * 抢拍
     */
    public static final String OPERATION_PLACE_AUCTION = "OPERATION_PLACE_AUCTION";

    /**
     * 领取优惠券
     */
    public static final String OPERATION_CLAIM_COUPON = "OPERATION_CLAIM_COUPON";
    /**
     * 购买优惠券
     */
    public static final String OPERATION_BUY_COUPON = "OPERATION_BUY_COUPON";

    /**
     * 取消下单
     */
    public static final String OPERATION_CANCEL_ORDER = "OPERATION_CANCEL_ORDER";
    /**
     * 退款
     */
    public static final String OPERATION_REFUND_ORDER = "OPERATION_REFUND_ORDER";

    /**
     * 退款
     */
    public static final String OPERATION_USE_SERVICE = "OPERATION_USE_SERVICE";

    public static final String OPERATION_CONFIRM_PICK_UP = "OPERATION_CONFIRM_PICK_UP";
    /**
     * 提现
     */
    public static final String OPERATION_ADMIN_HANDLE_WITHDRAW = "OPERATION_ADMIN_HANDLE_WITHDRAW";

    /**
     * 短信限制时效，目前为一天
     */
    public static final int KEY_EXPIRE_TIME_24H = 86400;
    /**
     * 2小时后失效
     */
    public static final int KEY_EXPIRE_TIME_2H = 7200;

    /**
     * 60秒失效时间
     */
    public static final int KEY_EXPIRE_TIME_60S = 60;

    /**
     * 5分失效时间
     */
    public static final int KEY_EXPIRE_TIME_5_MIN = 300;
    /**
     * 5分失效时间
     */
    public static final int KEY_EXPIRE_TIME_30_MIN = 1800;

    /**
     * 首页文章标题最大长度
     */
    public static final int HOMEPAGE_ARTICLE_MAX_LEN = 25;

    public static final int PAGE_SIZE_30 = 30;
    public static final int PAGE_SIZE_20 = 20;
    public static final int PAGE_SIZE_10 = 10;

    /**
     * 认证头
     */
    public static final String KEY_AUTH_TOKEN = "X-AUTH-TOKEN";
    public static final String KEY_AUTH_TOKEN_UID = "X-AUTH-TOKEN-UID";
    public static final String KEY_AUTH_TOKEN_BY_UID = "X-AUTH-TOKEN-BY-UID:";
    public static final String KEY_AUTH_TOKEN_COOKIE = "X-AUTH-TOKEN-COOKIE";

    /**
     * 最后一次发送的短信验证码
     */
    public static final String KEY_LAST_VERIFICATION_CODE_PREFIX_KEY = "KEY_LAST_VERIFICATION_CODE_PREFIX_KEY:";

    /**
     * 交易类型,充值
     */
    public static final int TRANSACTION_TYPE_DEPOSIT = 1;

    /**
     * 交易类型,系统取消订单
     */
    public static final int TRANSACTION_TYPE_CANCEL_ORDER_BY_SYSTEM = 2;

    /**
     * 交易类型,系统后台设置
     */
    public static final int TRANSACTION_TYPE_SYSTEM = 3;


    /**
     * 交易类型,交易扣除
     */
    public static final int TRANSACTION_TYPE_PLACE_ORDER = 5;
    /**
     * 交易类型,下单赚送积分
     */
    public static final int TRANSACTION_TYPE_GIVE_SCORE_FOR_ORDER = 6;
    /**
     * 交易类型,积分支付订单
     */
    public static final int TRANSACTION_TYPE_SCORE_PAY_FOR_ORDER = 7;

    /**
     * 交易类型,充值赠送
     */
    public static final int TRANSACTION_TYPE_GIVE_FOR_CHARGE = 8;

    /**
     * 交易类型,取消订单
     */
    public static final int TRANSACTION_TYPE_CANCEL_ORDER = 9;

    /**
     * 交易类型,拼团失败，退款
     */
    public static final int TRANSACTION_TYPE_GROUPBUY_FAILED_REFUND_MONEY = 10;

    /**
     * 交易类型,取消订单扣除赠送的积分
     */
    public static final int TRANSACTION_TYPE_SUBTRACT_SCORE_GAVE_FOR_CANCEL_ORDER = 11;


    //订单抽成
    public static final int TRANSACTION_TYPE_DRAW_COMMISSION = 13;

    //返现到冻结中
    public static final int TRANSACTION_TYPE_RETURN_COMMISSION_FREEZE = 15;

    public static final int TRANSACTION_TYPE_WITHDRAW = 16;
    public static final int TRANSACTION_TYPE_WITHDRAW_DENY_REFUND = 17;
    public static final int TRANSACTION_TYPE_AGREE_WITHDRAW = 18;

    public static final int TRANSACTION_BUY_SERVICE = 30;
    public static final int TRANSACTION_BUY_MEMBERSHIP = 31;
    public static final int TRANSACTION_USE_SERVICE = 32;
    public static final int TRANSACTION_ORDER_AWARD = 33;
    public static final int TRANSACTION_PICK_UP_CONFIRM = 34;

    public static final int TRANSACTION_TYPE_USER_PLACE_ORDER_AWARD_INVITER = 97;
    public static final int TRANSACTION_TYPE_USER_PLACE_ORDER_AWARD_TEAM_LEADER = 98;
    public static final int TRANSACTION_TYPE_CREDIT_SCORE = 99;
    public static final int TRANSACTION_TYPE_GROUPON_REFUND = 100;
    public static final int TRANSACTION_TYPE_ORDER_CANCEL_CALL_BACK_AWARD = 101;
    public static final int TRANSACTION_TYPE_ORDER_CONFIRM_EXECUTE_AWARD = 102;
    public static final int TRANSACTION_TYPE_GIVE_FREE_MEMBER_SHIP = 106;
    public static final int TRANSACTION_TYPE_AWARD_INVITE = 120;
    public static final int TRANSACTION_TYPE_AUTH_AWARD = 130;
    public static final int MAX_COUNT_INVOKE_IS_VALID_ACCOUNT_PER_DAY = 50;


    /**
     * 以下为系统参数配置名字
     */
    //首充达到普通会员所需要的充值金额
    public static final String PARAM_KEY_FIRST_CHARGE_TO_NORMAL_LEVEL = "FIRST_CHARGE_TO_NORMAL_LEVEL";
    //首充达到高级会员所需要的充值金额
    public static final String PARAM_KEY_FIRST_CHARGE_TO_ADVANCE_LEVEL = "FIRST_CHARGE_TO_ADVANCE_LEVEL";
    //首充达到钻石会员所需要的充值金额
    public static final String PARAM_KEY_FIRST_CHARGE_TO_DIAMOND_LEVEL = "FIRST_CHARGE_TO_DIAMOND_LEVEL";
    //首充达到至尊会员所需要的充值金额
    public static final String PARAM_KEY_FIRST_CHARGE_TO_TOP_LEVEL = "FIRST_CHARGE_TO_TOP_LEVEL";

    //统计积分的天数来判断是否需要升级，暂定90天
    public static final String PARAM_KEY_DAYS_FOR_SCORE_TO_UPDATE = "DAYS_FOR_SCORE_TO_UPDATE";
    //达到普通会员所需要的消费金额
    public static final String PARAM_KEY_CONSUME_MONEY_TO_NORMAL_LEVEL = "CONSUME_MONEY_TO_NORMAL_LEVEL";
    //达到高级会员所需要的消费金额
    public static final String PARAM_KEY_CONSUME_MONEY_TO_ADVANCE_LEVEL = "CONSUME_MONEY_TO_ADVANCE_LEVEL";
    //达到钻石会员所需要的消费金额
    public static final String PARAM_KEY_CONSUME_MONEY_TO_DIAMOND_LEVEL = "CONSUME_MONEY_TO_DIAMOND_LEVEL";
    //达到至尊会员所需要的消费金额
    public static final String PARAM_KEY_CONSUME_MONEY_TO_TOP_LEVEL = "CONSUME_MONEY_TO_TOP_LEVEL";

    //普通会员生日折扣
    public static final String PARAM_KEY_BIRTHDAY_DISCOUNT_FOR_NORMAL_LEVEL = "BIRTHDAY_DISCOUNT_FOR_NORMAL_LEVEL";
    //高级会员生日折扣
    public static final String PARAM_KEY_BIRTHDAY_DISCOUNT_FOR_ADVANCE_LEVEL = "BIRTHDAY_DISCOUNT_FOR_ADVANCE_LEVEL";
    //钻石会员生日折扣
    public static final String PARAM_KEY_BIRTHDAY_DISCOUNT_FOR_DIAMOND_LEVEL = "BIRTHDAY_DISCOUNT_FOR_DIAMOND_LEVEL";
    //至尊会员生日折扣
    public static final String PARAM_KEY_BIRTHDAY_DISCOUNT_FOR_TOP_LEVEL = "BIRTHDAY_DISCOUNT_FOR_TOP_LEVEL";


    //普通会员消费获得积分
    public static final String PARAM_KEY_CONSUME_GOT_SCORE_FOR_NORMAL_LEVEL = "CONSUME_GOT_SCORE_FOR_NORMAL_LEVEL";
    //高级会员消费获得积分
    public static final String PARAM_KEY_CONSUME_GOT_SCORE_FOR_ADVANCE_LEVEL = "CONSUME_GOT_SCORE_FOR_ADVANCE_LEVEL";
    //钻石会员消费获得积分
    public static final String PARAM_KEY_CONSUME_GOT_SCORE_FOR_DIAMOND_LEVEL = "CONSUME_GOT_SCORE_FOR_DIAMOND_LEVEL";
    //至尊会员消费获得积分
    public static final String PARAM_KEY_CONSUME_GOT_SCORE_FOR_TOP_LEVEL = "CONSUME_GOT_SCORE_FOR_TOP_LEVEL";

    //充300送多少
    public static final String PARAM_KEY_GIVE_MONEY_WHEN_UPTO_300 = "GIVE_MONEY_WHEN_UPTO_300";
    //充500送多少
    public static final String PARAM_KEY_GIVE_MONEY_WHEN_UPTO_500 = "GIVE_MONEY_WHEN_UPTO_500";
    //充1000送多少
    public static final String PARAM_KEY_GIVE_MONEY_WHEN_UPTO_1000 = "GIVE_MONEY_WHEN_UPTO_1000";
    //充2000送多少
    public static final String PARAM_KEY_GIVE_MONEY_WHEN_UPTO_2000 = "GIVE_MONEY_WHEN_UPTO_2000";
    //充5000送多少
    public static final String PARAM_KEY_GIVE_MONEY_WHEN_UPTO_5000 = "GIVE_MONEY_WHEN_UPTO_5000";
    //充10000送多少
    public static final String PARAM_KEY_GIVE_MONEY_WHEN_UPTO_10000 = "GIVE_MONEY_WHEN_UPTO_10000";
    //公司名字
    public static final String PARAM_KEY_COMPANY_NAME = "PARAM_KEY_COMPANY_NAME";
    public static final String PARAM_KEY_CONTACT_NAME = "PARAM_KEY_CONTACT_NAME";
    public static final String PARAM_KEY_CONTACT_PHONE_NUMBER = "PARAM_KEY_CONTACT_PHONE_NUMBER";
    public static final String PARAM_KEY_CONTACT_POST_CODE = "PARAM_KEY_CONTACT_POST_CODE";
    public static final String PARAM_KEY_CONTACT_PROVINCE = "PARAM_KEY_CONTACT_PROVINCE";
    public static final String PARAM_KEY_CONTACT_CITY = "PARAM_KEY_CONTACT_CITY";
    public static final String PARAM_KEY_CONTACT_AREA = "PARAM_KEY_CONTACT_AREA";
    public static final String PARAM_KEY_CONTACT_ADDRESS = "PARAM_KEY_CONTACT_ADDRESS";

    public static final String KEY_LIMIT_IP_REQUEST_KEYSET = "KEY_LIMIT_IP_REQUEST_KEYSET";

    public static final int QUERY_TYPE_CHARGE = 1;
    public static final int QUERY_TYPE_CONSUME = 2;

    public static final String PARAM_KEY_IMAGE_SHOW_DOMAIN = "IMAGE_SHOW_DOMAIN";

    //股权总数
    public static final String PARAM_KEY_TOTAL_SHARE = "PARAM_KEY_TOTAL_SHARE";
    //已分配股权
    public static final String PARAM_KEY_SHARE_ALREADY_RELEASE = "PARAM_KEY_SHARE_ALREADY_RELEASE";

    //直接推广上级抽成比例
    public static final String PARAM_KEY_MASTER_COMMISSION_RATIO = "PARAM_KEY_MASTER_COMMISSION_RATIO";
    //推广上上级抽成比例
    public static final String PARAM_KEY_MASTER_MASTER_COMMISSION_RATIO = "PARAM_KEY_MASTER_MASTER_COMMISSION_RATIO";
    //购买的积分返现
    public static final String PARAM_KEY_ORDER_RETURN_COMMISSION_RATIO = "PARAM_KEY_ORDER_RETURN_COMMISSION_RATIO";
    //团队返现比例
    public static final String PARAM_KEY_PLATFORM_COMMISSION_RATIO = "PARAM_KEY_PLATFORM_COMMISSION_RATIO";
    public static final String PARAM_KEY_TIME_STAMP_INSTANCE = "PARAM_KEY_TIME_STAMP_INSTANCE";

    public static final String TASK_NEW_ORDER = "TASK_NEW_ORDER";
    public static final String TASK_NEW_POST_SERVICE = "TASK_NEW_POST_SERVICE";
    public static final String TASK_NEW_CUSTOMER = "TASK_NEW_CUSTOMER";
    public static final String TASK_NEED_APPROVE = "TASK_NEED_APPROVE";
    public static final String PING = "PING";
    public static final String PONG = "PONG";

    public static final String PARAM_KEY_AWARD_DEALER_FOR_REG = "PARAM_KEY_AWARD_DEALER_FOR_REG";
    public static final String PARAM_KEY_AWARD_DEALER_FOR_BUY_VIP = "PARAM_KEY_AWARD_DEALER_FOR_BUY_VIP";
    public static final String PARAM_KEY_AWARD_DEALER_FOR_PLACE_ORDER = "PARAM_KEY_AWARD_DEALER_FOR_PLACE_ORDER";
    public static final String PARAM_KEY_AWARD_CUSTOMER_FOR_EVERYDAY_SIGN_IN = "PARAM_KEY_AWARD_CUSTOMER_FOR_EVERYDAY_SIGN_IN";

    public static final String PARAM_KEY_AWARD_DIRECT_DEALER_PERCENTAGE = "PARAM_KEY_AWARD_DIRECT_DEALER_PERCENTAGE";
    public static final String PARAM_KEY_AWARD_INDIRECT_DEALER_PERCENTAGE = "PARAM_KEY_AWARD_INDIRECT_DEALER_PERCENTAGE";
    public static final String PARAM_KEY_AWARD_SELF_TAKEN_PLACE_PERCENTAGE = "PARAM_KEY_AWARD_SELF_TAKEN_PLACE_PERCENTAGE";

    public static final int DEFAULT_AWARD_DEALER_FOR_BUY_VIP = 3000;
    public static final double DEFAULT_AWARD_DEALER_FOR_PLACE_ORDER = 0.05;
    public static final String CARD_COUPON_SYSTEM_GIVE_CODE = "SYSTEM_GIVE";

    public static final String PARAM_KEY_WECHAT_MP_APP_ID = "PARAM_KEY_WECHAT_MP_APP_ID";
    public static final String PARAM_KEY_WECHAT_MP_SECRET_CODE = "PARAM_KEY_WECHAT_MP_SECRET_CODE";
    public static final String PARAM_KEY_WECHAT_MINI_APP_ID = "PARAM_KEY_WECHAT_MINI_APP_ID";
    public static final String PARAM_KEY_WECHAT_MINI_APP_SECRET_CODE = "PARAM_KEY_WECHAT_MINI_APP_SECRET_CODE";
    public static final String PARAM_KEY_WECHAT_MCH_ID = "PARAM_KEY_WECHAT_MCH_ID";
    public static final String PARAM_KEY_ALI_YUN_ACCESS_ID = "PARAM_KEY_ALI_YUN_ACCESS_ID";
    public static final String PARAM_KEY_ALI_YUN_SECRET_KEY = "PARAM_KEY_ALI_YUN_SECRET_KEY";
    public static final String PARAM_KEY_WECHAT_MCH_API_SECURITY_CODE = "PARAM_KEY_WECHAT_MCH_API_SECURITY_CODE";
    public static final String PARAM_KEY_DEFAULT_HOME_PAGE_URL = "PARAM_KEY_DEFAULT_HOME_PAGE_URL";

    public static final long DEFAULT_MAX_TIME_FOR_RETURN_GOODS = 7 * 24 * 3600;
    public static final long DEFAULT_MAX_TIME_FOR_AUDIT_RETURN = 2 * 24 * 3600;
    public static final long DEFAULT_MAX_TIME_FOR_REFUND = 7 * 24 * 3600;
    public static final String PARAM_KEY_MAX_TIME_FOR_RETURN_GOODS = "PARAM_KEY_MAX_TIME_FOR_RETURN_GOODS";
    public static final String PARAM_KEY_MAX_TIME_FOR_AUDIT = "PARAM_KEY_MAX_TIME_FOR_AUDIT";
    public static final String PARAM_KEY_MAX_TIME_FOR_REFUND = "PARAM_KEY_MAX_TIME_FOR_REFUND";

    public static final String PARAM_KEY_ALIPAY_APPID = "PARAM_KEY_ALIPAY_APPID";
    public static final String PARAM_KEY_ALIPAY_APP_PRIVATE_KEY = "PARAM_KEY_ALIPAY_APP_PRIVATE_KEY";
    public static final String PARAM_KEY_ALIPAY_ALI_PUBLIC_KEY_RSA2 = "PARAM_KEY_ALIPAY_ALI_PUBLIC_KEY_RSA2";
    public static final String PARAM_KEY_ALIPAY_ALI_PUBLIC_KEY = "PARAM_KEY_ALIPAY_ALI_PUBLIC_KEY";
    public static final String PARAM_KEY_ALIPAY_WAP_PAY_NOTIFY_URL = "PARAM_KEY_ALIPAY_WAP_PAY_NOTIFY_URL";
    public static final String PARAM_KEY_ALIPAY_DIRECT_PAY_NOTIFY_URL = "PARAM_KEY_ALIPAY_DIRECT_PAY_NOTIFY_URL";
    public static final String PARAM_KEY_ALIPAY_RETURN_URL = "PARAM_KEY_ALIPAY_RETURN_URL";
    public static final String PARAM_KEY_ALIPAY_PARTNER_NO = "PARAM_KEY_ALIPAY_PARTNER_NO";

    public static final String PARAM_KEY_KD_HOST_URL = "PARAM_KEY_KD_HOST_URL";
    public static final String PARAM_KEY_KD_APP_CODE = "PARAM_KEY_KD_APP_CODE";
    public static final String NEW_SHOP_AUTH_AWARD = "NEW_SHOP_AUTH_AWARD";
    public static final String NEW_USE_PERSONAL_AWARD = "NEW_USE_PERSONAL_AWARD";
}
