package constants;

/**
 * Redis key常量
 * Created by win7 on 2016/6/11.
 */
public class RedisKeyConstant {
    /**
     * 短信TOKEN
     */
    public static final String SMS_TOKEN_KEY = "SMS_TOKEN_KEY";

    /**
     * 系统身份认证token
     */
    public static final String AUTH_TOKEN = "authToken";

    /**
     * 放在缓存中的用户id 目前使用双向缓存，通过token能找到用户id，通过id也能找到对应的缓存
     */
    public static final String AUTH_TOKEN_UID = "AUTH_TOKEN_UID";

    /**
     * 放在缓存中的管理员token id 目前使用双向缓存，通过token能找到用户id，通过id也能找到对应的缓存
     */
    public static final String AUTH_ADMIN_TOKEN_UID = "AUTH_ADMIN_TOKEN_UID";

    /**
     * 所有币种集自己的集合
     */
    public static final String KEY_ALL_CURRENCIES_KEYSET = "KEY_ALL_CURRENCIES_KEYSET";

    /**
     * 所有币种基本不会变的信息缓存,ZADD类型，缓存类型为有序集合
     */
    public static final String KEY_CURRENCY_DETAIL_ALL_LIST_KEYSET = "KEY_CURRENCY_DETAIL_ALL_LIST_KEYSET";

    /**
     * 币种列表，返回的数据进行缓存
     */
    public static final String KEY_CACHE_CURRENCY_SIMPLE_LIST_DATA = "KEY_CACHE_CURRENCY_LIST_DATA";


    /**
     * 交易临时并发锁集合
     */
    public static final String TRADE_LOCK_KEY_SET = "TRADE_LOCK_KEY_SET:";
    /**
     * 币种hkey的前缀
     */
    public static final String CURRENCY_DETAIL_KEY_PREFIX = "currency:detail:";

    /**
     * 统计数据中的key前缀
     */
    public static final String TRADE_KEY_STATISTICS_PREFIX = "trade:statistics:";

    /**
     * 统计数据中的key集合关键字
     */
    public static final String TRADE_STATISTICS_KEY_SET = "TRADE_STATISTICS_KEY_SET";

    /**
     * 24小时成交量
     */
    public static final String TRADE_STATISTICS_KEY_24H_DEAL_AMOUNT_PREFIX = "TRADE_STATISTICS_KEY_24H_DEAL_AMOUNT:";
    /**
     * 24小时成交金额
     */
    public static final String TRADE_STATISTICS_KEY_24H_DEAL_MONEY_PREFIX = "TRADE_STATISTICS_KEY_24H_DEAL_MONEY_PREFIX:";

    /**
     * 我的推广明细总页数缓存
     */
    public static final String KEY_MY_PROMOTION_PAGES = "KEY_MY_PROMOTION_PAGES";

    /**
     * 首页信息栏跟链接栏缓存
     */
    public static final String KEY_HOME_PAGE_INFO_LINKS = "KEY_HOME_PAGE_INFO_LINKS";
    /**
     * 首页信息栏跟链接栏缓存
     */
    public static final String KEY_ARTICLE_FOR_NEWBIE = "KEY_ARTICLE_FOR_NEWBIE";

    /**
     * 底部信息栏缓存
     */
    public static final String KEY_HOME_PAGE_BOTTOM_INFO_LINKS = "KEY_HOME_PAGE_BOTTOM_INFO_LINKS";

    /**
     * 首页信息栏跟链接栏缓存
     */
    public static final String KEY_CAROUSEL_PREFIX = "KEY_CAROUSEL_PREFIX:";

    /**
     * 配置缓存key,以string方式
     */
    public static final String KEY_WEBSITE_CONFIG = "KEY_WEBSITE_CONFIG";

    /**
     * 用户表缓存
     */
    public static final String KEY_MEMBER_ACCOUNT_NAME = "KEY_MEMBER_ACCOUNT_NAME";
    public static final String KEY_MEMBER_ID = "KEY_MEMBER_ID";
    public static final String KEY_MEMBER_TOKEN_KEY = "KEY_MEMBER_TOKEN_KEY:";
    public static final String KEY_MEMBER_ACCOUNT_STATUS = "KEY_MEMBER_ACCOUNT_STATUS";
    public static final String KEY_MEMBER_PLATFORM_TOKEN_KEY = "KEY_MEMBER_PLATFORM_TOKEN_KEY:";
    public static final String KEY_MEMBER_PHONE_NUMBER = "KEY_MEMBER_PHONE_NUMBER";
    public static final String KEY_MEMBER_REAL_NAME = "KEY_MEMBER_REAL_NAME";
    public static final String KEY_MEMBER_LEVEL = "KEY_MEMBER_LEVEL";

    /**
     * 系统配置表缓存,hget
     */
    public static final String KEY_SYSTEM_CONFIG = "KEY_SYSTEM_CONFIG";
    public static final String KEY_SYSTEM_CONFIG_WECHAT_KEY = "KEY_SYSTEM_CONFIG_WECHAT_KEY";
    public static final String KEY_SYSTEM_CONFIG_WECHAT_SECRET = "KEY_SYSTEM_CONFIG_WECHAT_SECRET";
    public static final String KEY_SYSTEM_CONFIG_ALIPAY_KEY = "KEY_SYSTEM_CONFIG_ALIPAY_KEY";
    public static final String KEY_SYSTEM_CONFIG_ALIPAY_SECRET = "KEY_SYSTEM_CONFIG_ALIPAY_SECRET";
    public static final String KEY_SYSTEM_CONFIG_SMS_ACCOUNT = "KEY_SYSTEM_CONFIG_SMS_ACCOUNT";
    public static final String KEY_SYSTEM_CONFIG_SMS_PWD = "KEY_SYSTEM_CONFIG_SMS_PWD";
    public static final String KEY_SYSTEM_CONFIG_GIVE_SCALE = "KEY_SYSTEM_CONFIG_GIVE_SCALE";
    public static final String KEY_SYSTEM_CONFIG_WITHDRAW = "KEY_SYSTEM_CONFIG_WITHDRAW";
    public static final String KEY_SYSTEM_CONFIG_WITHDRAW_RMB_MIN = "KEY_SYSTEM_CONFIG_WITHDRAW_RMB_MIN";
    public static final String KEY_SYSTEM_CONFIG_WITHDRAW_RMB_MAX = "KEY_SYSTEM_CONFIG_WITHDRAW_RMB_MAX";
    public static final String KEY_SYSTEM_CONFIG_DEPOSIT_MIN = "KEY_SYSTEM_CONFIG_DEPOSIT_MIN";
    public static final String KEY_SYSTEM_CONFIG_DEPOSIT_MAX = "KEY_SYSTEM_CONFIG_DEPOSIT_MAX";
    //    public static final String KEY_SYSTEM_CONFIG_BANK_2 = "2";
//    public static final String KEY_SYSTEM_CONFIG_BANK_ACCOUNT = "KEY_SYSTEM_CONFIG_BANK_ACCOUNT";
//    public static final String KEY_SYSTEM_CONFIG_BANK_NAME = "KEY_SYSTEM_CONFIG_BANK_NAME";
//    public static final String KEY_SYSTEM_CONFIG_BANK_USER_NAME = "KEY_SYSTEM_CONFIG_BANK_USER_NAME";
    public static final String KEY_SYSTEM_CONFIG_PROMOT = "KEY_SYSTEM_CONFIG_PROMOT";
    public static final String KEY_SYSTEM_CONFIG_PROMOT_GIFT_NUM = "KEY_SYSTEM_CONFIG_PROMOT_GIFT_NUM";
    public static final String KEY_SYSTEM_CONFIG_PROMOT_GIFT = "KEY_SYSTEM_CONFIG_PROMOT_GIFT";
    public static final String KEY_SYSTEM_CONFIG_PROMOT_TYPE = "KEY_SYSTEM_CONFIG_PROMOT_TYPE";
    public static final String KEY_SYSTEM_CONFIG_COIN_WITHDRAW = "KEY_SYSTEM_CONFIG_COIN_WITHDRAW";
    public static final String KEY_SYSTEM_CONFIG_COIN_WITHDRAW_MIN = "KEY_SYSTEM_CONFIG_COIN_WITHDRAW_MIN";
    //这是人民币最低提现的手续费，由于历史原因名字搞成了币种名字，请注意
    public static final String KEY_SYSTEM_CONFIG_COIN_WITHDRAW_CHARGE_FEE_MIN = "KEY_SYSTEM_CONFIG_COIN_WITHDRAW_CHARGE_FEE_MIN";
    public static final String KEY_SYSTEM_CONFIG_COIN_WITHDRAW_MAX = "KEY_SYSTEM_CONFIG_COIN_WITHDRAW_MAX";
    public static final String KEY_SYSTEM_CONFIG_PROFIT_RMB_WITHDRAW_FEE_SCALE = "KEY_SYSTEM_CONFIG_PROFIT_RMB_WITHDRAW_FEE_SCALE";
    public static final String KEY_SYSTEM_CONFIG_COIN_PROFIT = "KEY_SYSTEM_CONFIG_COIN_PROFIT";
    public static final String KEY_SYSTEM_CONFIG_KATE_SUB = "KEY_SYSTEM_CONFIG_KATE_SUB";
    public static final String KEY_SYSTEM_CONFIG_PROFIT_WITHDRAW_COIN_FEE_SCALE = "KEY_SYSTEM_CONFIG_PROFIT_WITHDRAW_COIN_FEE_SCALE";

    /**
     * 限制访问是否是有效用户的请求接口，目前限制次数为10
     */
    public static final String KEY_INVOKE_IS_VALID_ACCOUNT_LIMIT = "KEY_INVOKE_IS_VALID_ACCOUNT_LIMIT:";

    /**
     * 邮箱登录密码重置码
     */
    public static final String KEY_MAIL_RESET_LOGIN_PASSWORD_VCODE = "KEY_MAIL_RESET_LOGIN_PASSWORD_VCODE:";
    /**
     * 邮箱支付密码重置码
     */
    public static final String KEY_MAIL_RESET_PAY_PASSWORD_VCODE = "KEY_MAIL_RESET_PAY_PASSWORD_VCODE:";

    /**
     * 绑定邮箱激活码
     */
    public static final String KEY_BIND_MAIL_VCODE = "KEY_BIND_MAIL_VCODE:";

    /**
     * 币种id跟简写映射
     */
    public static final String KEY_CURRENCY_ID_ABBR_MAP = "KEY_CURRENCY_ID_ABBR_MAP:";

    /**
     * 返回前端url
     */
    public static final String KEY_RETURN_URL_FOR_WEB = "KEY_RETURN_URL_FOR_WEB:";

    /**
     * 是否在维护当中
     */
    public static final String KEY_SYSTEM_UNDER_MAINTAINCE = "KEY_SYSTEM_UNDER_MAINTAINCE";

    /**
     * 注册活动hashkey
     */
    public static final String KEY_REG_ACTIVITY = "KEY_REG_ACTIVITY";
    public static final String KEY_REG_ACTIVITY_NAME = "KEY_REG_ACTIVITY_NAME";
    public static final String KEY_REG_ACTIVITY_CURRENCY_ID = "KEY_REG_ACTIVITY_CURRENCY_ID";
    public static final String KEY_REG_ACTIVITY_START_TIME = "KEY_REG_ACTIVITY_START_TIME";
    public static final String KEY_REG_ACTIVITY_END_TIME = "KEY_REG_ACTIVITY_END_TIME";
    public static final String KEY_REG_ACTIVITY_STATUS = "KEY_REG_ACTIVITY_STATUS";
    public static final String KEY_REG_ACTIVITY_DESCRIPTION = "KEY_REG_ACTIVITY_DESCRIPTION";
    public static final String KEY_REG_ACTIVITY_AWARD_TO_REG_USER = "KEY_REG_ACTIVITY_AWARD_TO_REG_USER";
    public static final String KEY_REG_ACTIVITY_AWARD_TO_INVITE_USER = "KEY_REG_ACTIVITY_AWARD_TO_INVITE_USER";

    /**
     * 注册模块生成校验码
     */
    public static final String KEY_SMS_SECURE_CODE = "KEY_SMS_SECURE_CODE:";
    /**
     * 发送短信ip
     */
    public static final String KEY_SMS_IP_PREFIX = "KEY_SMS_IP_PREFIX:";
    public static final String KEY_LOGIN_MAX_ERROR_TIMES = "KEY_LOGIN_MAX_ERROR_TIMES:";
    public static final String KEY_LOGIN_PREFIX = "KEY_LOGIN_PREFIX:";

    public static final String KEY_REG_IP_PREFIX = "KEY_REG_IP_PREFIX:";
    public static final String CURRENCY_OFFER_PREFIX = "CURRENCY_OFFER_PREFIX:";

    /**
     * 活动redis key，用于记录剩余数量
     */
    public static final String ACTIVITY_AWARD_LEFT_AMOUNT_PREFIX_KEY = "ACTIVITY_AWARD_LEFT_AMOUNT_PREFIX_KEY:";

    /**
     * 短信限制前缀
     */
    public static final String SMS_PHONENUMBER_LIMIT_PREFIX_KEY = "SMS_PHONENUMBER_LIMIT_PREFIX_KEY:";

    public static final String INTEREST_KEYS_SET = "INTEREST_kEYS_SET";
    public static final String INTEREST_KEY_EACH_PREFIX = "INTEREST_KEY_EACH_PREFIX:";
    public static final String INTEREST_KEY_ITEM_INTEREST = "INTEREST_KEY_ITEM_INTEREST:";


    /**
     * 网站存量key前缀
     */
    public static final String TOTAL_BALANCE_KEY_PREFIX = "TOTAL_BALANCE_KEY_PREFIX:";
    /**
     * 所有币种利息集合
     */
    public static final String ALL_INTERESTS_KEY_SET = "ALL_INTERESTS_KEY_SET";
    /**
     * 所有用户等级的集合
     */
    public static final String ALL_LEVELS_KEY_SET = "ALL_LEVELS_KEY_SET";

    /**
     * 每个等级的key
     */
    public static final String LEVEL_KEY_PREFIX = "LEVEL_KEY_PREFIX:";

    public static final String LEVEL_KEY_NEED_SCORE = "LEVEL_KEY_NEED_SCORE";
    public static final String LEVEL_KEY_LEVEL = "LEVEL_KEY_LEVEL";
    public static final String LEVEL_KEY_LEVEL_NAME = "LEVEL_KEY_LEVEL_NAME";
    public static final String LEVEL_KEY_ORDER_DISCOUNT = "LEVEL_KEY_ORDER_DISCOUNT";

    /**
     * 所有积分配置的集合
     */
    public static final String ALL_SCORE_CONFIGS_KEY_SET = "ALL_SCORE_CONFIGS_KEY_SET";

    /**
     * 每个积分配置的key
     */
    public static final String SCORE_CONFIG_KEY_PREFIX = "SCORE_CONFIG_KEY_PREFIX:";

    public static final String SCORE_CONFIG_KEY_TYPE = "SCORE_CONFIG_KEY_TYPE";
    public static final String SCORE_CONFIG_KEY_SCORE = "SCORE_CONFIG_KEY_SCORE";

    /**
     * 是否允许抽奖
     */
    public static final String LUCKY_KEY_ENABLE = "LUCKY_KEY_ENABLE";

    /**
     * 每日抽奖允许次数
     */
    public static final String LUCKY_KEY_MAX_PER_DAY = "LUCKY_KEY_MAX_PER_DAY";
    /**
     * 奖品集合
     */
    public static final String LUCKY_KEY_PRIZE_KEY_SET = "LUCKY_KEY_PRIZE_KEY_SET";
    /**
     * 每个奖品项的key前缀
     */
    public static final String LUCKY_KEY_PRIZE_EACH_KEY_PREFIX = "LUCKY_KEY_PRIZE_EACH_KEY_PREFIX:";

    /**
     * 奖品id
     */
    public static final String LUCKY_KEY_ID = "LUCKY_KEY_ID";
    /**
     * 奖品类别
     */
    public static final String LUCKY_KEY_PRIZE_CLASS = "LUCKY_KEY_PRIZE_CLASS";
    /**
     * 奖品名字
     */
    public static final String LUCKY_KEY_PRIZE_CLASS_NAME = "LUCKY_KEY_PRIZE_CLASS_NAME";
    /**
     * 中奖概率
     */
    public static final String LUCKY_KEY_PRIZE_CHANCE = "LUCKY_KEY_PRIZE_CHANCE";
    /**
     * 最小内角度
     */
    public static final String LUCKY_KEY_INNER_MIN_ANGEL = "LUCKY_KEY_INNER_MIN_ANGEL";
    /**
     * 最大内角度
     */
    public static final String LUCKY_KEY_INNER_MAX_ANGEL = "LUCKY_KEY_INNER_MAX_ANGEL";
    /**
     * 最小外角度
     */
    public static final String LUCKY_KEY_OUTER_MIN_ANGEL = "LUCKY_KEY_OUTER_MIN_ANGEL";
    /**
     * 最大外角度
     */
    public static final String LUCKY_KEY_OUTER_MAX_ANGEL = "LUCKY_KEY_OUTER_MAX_ANGEL";


    /**
     * 该等级奖品数量
     */
    public static final String LUCKY_KEY_AMOUNT = "LUCKY_KEY_AMOUNT";

    /**
     * 虚拟币奖励的币种
     */
    public static final String LUCKY_KEY_PRIZE_CURRENCY_ID = "LUCKY_KEY_PRIZE_CURRENCY_ID";

    /**
     * 虚拟币奖励的数量
     */
    public static final String LUCKY_KEY_PRIZE_CURRENCY_AMOUNT = "LUCKY_KEY_PRIZE_CURRENCY_AMOUNT";

    /**
     * 兑换的缓存key
     */
    public static final String EXCHANGE_MERCHANTS_LIST_CACHE_KEY_SET = "EXCHANGE_MERCHANTS_LIST_CACHE_KEY_SET";
    /**
     * 每一页单独缓存
     */
    public static final String EXCHANGE_MERCHANTS_LIST_CACHE_KEY_PREFIX = "EXCHANGE_MERCHANTS_LIST_CACHE_KEY_PREFIX:";
    /**
     * 推荐商品列表缓存
     */
    public static final String EXCHANGE_RECOMMEND_MERCHANTS_LIST_CACHE_KEY_PREFIX = "EXCHANGE_RECOMMEND_MERCHANTS_LIST_CACHE_KEY_PREFIX:";


    /**
     * 商品分类的缓存key
     */
    public static final String MERCHANTS_CATEGORIES_JSON_CACHE_LIST = "MERCHANTS_CATEGORIES_JSON_CACHE_LIST";
    public static final String MERCHANTS_CATEGORIES_EACH_PREFIX = "MERCHANTS_CATEGORIES_EACH_PREFIX:";
    public static final String WINE_CATEGORIES_EACH_PREFIX = "WINE_CATEGORIES_EACH_PREFIX:";
    /**
     * 分类每一页单独缓存
     */
    public static final String MERCHANTS_CATEGORIES_LIST_CACHE_KEY_PREFIX = "MERCHANTS_CATEGORIES_LIST_CACHE_KEY_PREFIX:";
    public static final String SHOP_PRODUCT_CATEGORIES_LIST_CACHE_KEY_PREFIX = "SHOP_PRODUCT_CATEGORIES_LIST_CACHE_KEY_PREFIX:";

    public static final String MERCHANTS_SOLD_AMOUNT_CACHE_KEY = "MERCHANTS_SOLD_AMOUNT_CACHE_KEY:";
    public static final String GRAB_TREASURE_SOLD_AMOUNT_CACHE_KEY = "GRAB_TREASURE_SOLD_AMOUNT_CACHE_KEY:";

    /**
     * 认购列表cache key的集合
     */
    public static final String OFFER_LIST_CACHE_KEY_SET = "OFFER_LIST_CACHE_KEY_SET";
    /**
     * 认购列表cache key
     */
    public static final String OFFER_LIST_CACHE_KEY_PREFIX = "OFFER_LIST_CACHE_KEY_PREFIX:";

    /**
     * 兑换的币种列表KEY
     */
    public static final String EXCHANGE_CURRENCY_LIST_KEY = "EXCHANGE_CURRENCY_LIST_KEY";

    public static final String GRAB_TREASURE_LUCKY_NUMBER_KEYSET_PREFIX = "GRAB_TREASURE_LUCKY_NUMBER_KEYSET_PREFIX:";


    public static final String LUCKY_CONFIG_KEY = "LUCKY_CONFIG_KEY:";

    /**
     * 币种兑换列表KEY
     */
    public static final String CURRENCY_EXCHANGE_CONFIG_LIST_KEY_SET = "CURRENCY_EXCHANGE_CONFIG_LIST_KEY_SET";

    /**
     * 每个兑换的前缀key
     */
    public static final String CURRENCY_EXCHANGE_AMOUNT_KEY = "CURRENCY_EXCHANGE_AMOUNT_KEY:";

    /**
     * 最新50条记录缓存
     */
    public static final String LATEST_50_LUCKY_LOGS = "LATEST_50_LUCKY_LOGS";

    /**
     * 品牌列表KEY
     */
    public static final String BRAND_KEY_SET = "BRAND_KEY_SET";

    public static final String MIX_OPTION_KEY_SET = "MIX_OPTION_KEY_SET";
    public static final String PRODUCT_CLASSIFY_KEY_SET = "PRODUCT_CLASSIFY_KEY_SET:";
    public static final String PRODUCT_CLASSIFY_JSON_CACHE = "PRODUCT_CLASSIFY_JSON_CACHE:";
    public static final String PRODUCT_CLASSIFY_BY_ID_JSON_CACHE = "PRODUCT_CLASSIFY_BY_ID_JSON_CACHE:";
    public static final String PRODUCT_CLASSIFY_LIST_JSON_CACHE = "PRODUCT_CLASSIFY_LIST_JSON_CACHE:";

    /**
     * 每个品牌的前缀key
     */
    public static final String BRAND_EACH_KEY = "BRAND_EACH_KEY:";
    public static final String BRAND_NAME = "BRAND_NAME";
    public static final String BRAND_LOGO = "BRAND_LOGO";
    public static final String BRAND_URL = "BRAND_URL";
    public static final String BRAND_POSTER = "BRAND_POSTER";
    public static final String HOMEPAGE_BRAND_JSON_CACHE = "HOMEPAGE_BRAND_JSON_CACHE";
    public static final String ON_NEW_ARRIVAL_JSON_CACHE = "ON_NEW_ARRIVAL_JSON_CACHE";
    public static final String ON_PROMOTION_JSON_CACHE = "ON_PROMOTION_JSON_CACHE";
    public static final String SEARCH_JSON_CACHE_ALL_LIST = "SEARCH_JSON_CACHE_ALL_LIST";
    public static final String SEARCH_JSON_CACHE_PREFIX = "SEARCH_JSON_CACHE_PREFIX:";

    public static final String WECHAT_ACCESS_TOKEN = "WECHAT_ACCESS_TOKEN";
    public static final String WECHAT_JS_TICKET = "WECHAT_JS_TICKET";
    public static final String ACCESS_URL_PREFIX = "ACCESS_URL_PREFIX:";
    public static final String QUESTIONAIRE_CONFIG_KEYSET = "QUESTIONAIRE_CONFIG_KEYSET";
    public static final String QUESTIONAIRE_CONFIG_KEY = "QUESTIONAIRE_CONFIG_KEY:";
    public static final String QUESTIONAIRE_CONFIG_KEY_ID = "QUESTIONAIRE_CONFIG_KEY_ID";
    public static final String QUESTIONAIRE_CONFIG_KEY_BEGIN_TIME = "QUESTIONAIRE_CONFIG_KEY_BEGIN_TIME";
    public static final String QUESTIONAIRE_CONFIG_KEY_END_TIME = "QUESTIONAIRE_CONFIG_KEY_END_TIME";
    public static final String QUESTIONAIRE_CONFIG_KEY_STATUS = "QUESTIONAIRE_CONFIG_KEY_STATUS";
    public static final String QUESTIONAIRE_CONFIG_KEY_TITLE = "QUESTIONAIRE_CONFIG_KEY_TITLE";
    public static final String QUESTIONAIRE_CONFIG_KEY_CONTENT = "QUESTIONAIRE_CONFIG_KEY_CONTENT";
    public static final String QUESTIONAIRE_JSON_CACHE_PREFIX = "QUESTIONAIRE_JSON_CACHE_PREFIX:";
    public static final String CAPTCHA_PREFIX = "CAPTCHA_PREFIX:";

    public static final String MAIL_FEE_CACHE_KEYSET = "MAIL_FEE_CACHE_KEYSET";
    public static final String MAIL_FEE_CACHE_KEY_PREFIX = "MAIL_FEE_CACHE_KEY_PREFIX:";
    public static final String MAIL_FEE_KEY_REGION_CODE = "MAIL_FEE_KEY_REGION_CODE";
    public static final String MAIL_FEE_KEY_REGION_NAME = "MAIL_FEE_KEY_REGION_NAME";
    public static final String MAIL_FEE_KEY_REGION_FEE = "MAIL_FEE_KEY_REGION_FEE";
    public static final String MAIL_FEE_KEY_REGION_CUSTOM_JSON = "MAIL_FEE_KEY_REGION_CUSTOM_JSON";


    //快递查询结果缓存
    public static final String DELIVERY_JSON_RESULT_CACHE_KEY = "DELIVERY_JSON_RESULT_CACHE_KEY:";
    public static final String DELIVERY_COMPANY_KEY_SET = "DELIVERY_COMPANY_KEY_SET:";
    public static final String DELIVERY_COMPANY_KEY_PREFIX = "DELIVERY_COMPANY_KEY:";
    public static final String DELIVERY_COMPANY_KEY_NAME = "DELIVERY_COMPANY_KEY_NAME";
    public static final String DELIVERY_COMPANY_KEY_TYPE = "DELIVERY_COMPANY_KEY_TYPE";
    public static final String DELIVERY_COMPANY_KEY_TEL = "DELIVERY_COMPANY_KEY_TEL";

    //优惠券
    public static final String COUPON_CONFIG_KEYSET = "COUPON_CONFIG_KEYSET";
    public static final String COUPON_CONFIG_KEY_PREFIX = "COUPON_CONFIG_KEY_PREFIX:";
    public static final String COUPON_CONFIG_KEY_ID = "COUPON_CONFIG_KEY_ID";
    public static final String COUPON_CONFIG_KEY_BEGIN_TIME = "COUPON_CONFIG_KEY_BEGIN_TIME";
    public static final String COUPON_CONFIG_KEY_END_TIME = "COUPON_CONFIG_KEY_END_TIME";
    public static final String COUPON_CONFIG_KEY_STATUS = "COUPON_CONFIG_KEY_STATUS";
    public static final String COUPON_CONFIG_KEY_TITLE = "COUPON_CONFIG_KEY_TITLE";
    public static final String COUPON_CONFIG_KEY_CONTENT = "COUPON_CONFIG_KEY_CONTENT";
    public static final String COUPON_CONFIG_AMOUNT = "COUPON_CONFIG_AMOUNT";
    public static final String COUPON_CONFIG_TOTAL_AMOUNT = "COUPON_CONFIG_TOTAL_AMOUNT";
    public static final String COUPON_CONFIG_TYPE = "COUPON_CONFIG_TYPE";
    public static final String COUPON_CONFIG_EXPIRE_DAYS = "COUPON_CONFIG_EXPIRE_DAYS";
    public static final String COUPON_CONFIG_CLAIM_LIMIT_PER_MEMBER = "COUPON_CONFIG_CLAIM_LIMIT_PER_MEMBER";
    public static final String COUPON_CONFIG_RULE_CONTENT = "COUPON_CONFIG_RULE_CONTENT";
    public static final String COUPON_CONFIG_MERCHANT_IDS = "COUPON_CONFIG_MERCHANT_IDS";
    public static final String COUPON_CONFIG_BRAND_IDS = "COUPON_CONFIG_BRAND_IDS";
    public static final String COUPON_CONFIG_ID_TYPE = "COUPON_CONFIG_ID_TYPE";
    public static final String COUPON_CONFIG_CLAIM_AMOUNT = "COUPON_CONFIG_CLAIM_AMOUNT";
    public static final String COUPON_CONFIG_OLD_PRICE = "COUPON_CONFIG_OLD_PRICE";
    public static final String COUPON_CONFIG_CURRENT_PRICE = "COUPON_CONFIG_CURRENT_PRICE";
    public static final String EXIST_REQUEST_SMS = "EXIST_REQUEST_SMS:";
    //系统参数配置
    public static final String PARAM_CONFIG_KEYSET = "PARAM_CONFIG_KEYSET";
    public static final String PARAM_CONFIG_KEY_PREFIX = "PARAM_CONFIG_KEY_PREFIX:";
    public static final String PARAM_CONFIG_KEY_ID = "COUPON_CONFIG_KEY_ID";
    public static final String PARAM_CONFIG_KEY_KEY = "PARAM_CONFIG_KEY_KEY";
    public static final String PARAM_CONFIG_KEY_VALUE = "PARAM_CONFIG_KEY_VALUE";

    //拼团
    public static final String GROUP_BUY_CONFIG_KEYSET = "GROUP_BUY_CONFIG_KEYSET";
    public static final String GROUP_BUY_CONFIG_KEY_PREFIX = "GROUP_BUY_CONFIG_KEY_PREFIX:";
    public static final String GROUP_BUY_CONFIG_KEY_PREFIX_BY_MERCHANT_ID = "GROUP_BUY_CONFIG_KEY_PREFIX_BY_MERCHANT_ID:";
    public static final String GROUP_BUY_CONFIG_KEY_ID = "GROUP_BUY_CONFIG_KEY_ID";
    public static final String GROUP_BUY_CONFIG_MERCHANT_ID = "GROUP_BUY_CONFIG_MERCHANT_ID";
    public static final String GROUP_BUY_CONFIG_KEY_STATUS = "GROUP_BUY_CONFIG_KEY_STATUS";
    public static final String GROUP_BUY_CONFIG_KEY_PRICE = "GROUP_BUY_CONFIG_KEY_PRICE";
    public static final String GROUP_BUY_CONFIG_KEY_BUYERS_NEED = "GROUP_BUY_CONFIG_KEY_BUYERS_NEED";
    public static final String GROUP_BUY_CONFIG_KEY_EXPIRE_HOURS = "GROUP_BUY_CONFIG_KEY_EXPIRE_HOURS";
    public static final String GROUP_BUY_CONFIG_KEY_BEGIN_TIME = "GROUP_BUY_CONFIG_KEY_BEGIN_TIME";
    public static final String GROUP_BUY_CONFIG_KEY_END_TIME = "GROUP_BUY_CONFIG_KEY_END_TIME";
    public static final String GROUP_BUY_CONFIG_KEY_CONTENT = "GROUP_BUY_CONFIG_KEY_CONTENT";

    //抢拍
    public static final String AUCTION_CONFIG_KEY_PREFIX = "AUCTION_CONFIG_KEY_PREFIX:";
    public static final String AUCTION_CONFIG_KEYSET = "AUCTION_CONFIG_KEYSET";
    public static final String AUCTION_CONFIG_KEY_BEGIN_PRICE = "AUCTION_CONFIG_KEY_BEGIN_PRICE";
    public static final String AUCTION_CONFIG_KEY_MIN_PRICE = "AUCTION_CONFIG_KEY_MIN_PRICE";
    public static final String AUCTION_CONFIG_KEY_ACTUAL_MIN_PRICE = "AUCTION_CONFIG_KEY_ACTUAL_MIN_PRICE";
    public static final String AUCTION_CONFIG_KEY_MAX_TIMES = "AUCTION_CONFIG_KEY_MAX_TIMES";
    public static final String AUCTION_CONFIG_KEY_TIME_INTERNAL = "AUCTION_CONFIG_KEY_TIME_INTERNAL";
    public static final String AUCTION_CONFIG_KEY_CONTENT = "AUCTION_CONFIG_KEY_CONTENT";
    public static final String AUCTION_CONFIG_KEY_MERCHANT_ID = "AUCTION_CONFIG_KEY_MERCHANT_ID";
    public static final String AUCTION_CONFIG_KEY_CONFIG_KEY_ID = "AUCTION_CONFIG_KEY_CONFIG_KEY_ID";

    //商品缓存
    public static final String MERCHANT_JSON_CACHE_KEY_PREFIX = "MERCHANT_JSON_CACHE_KEY_PREFIX:";
    //商品缓存
    public static final String MERCHANT_CACHE_KEY_PREFIX = "MERCHANT_CACHE_KEY_PREFIX:";
    public static final String MERCHANT_CACHE_KEYSET = "MERCHANT_CACHE_KEYSET";
    public static final String MERCHANT_KEY_ID = "MERCHANT_KEY_ID";
    public static final String MERCHANT_KEY_NAME = "MERCHANT_KEY_NAME";
    public static final String MERCHANT_KEY_THUMB_COVER_IMG_URL = "MERCHANT_KEY_THUMB_COVER_IMG_URL";
    public static final String MERCHANT_KEY_STOCK = "MERCHANT_KEY_STOCK";
    public static final String MERCHANT_KEY_COVER_IMG_URL = "MERCHANT_KEY_COVER_IMG_URL";
    public static final String MERCHANT_KEY_CATEGORY_ID = "MERCHANT_KEY_CATEGORY_ID";
    public static final String MERCHANT_KEY_BRAND_ID = "MERCHANT_KEY_BRAND_ID";
    public static final String MERCHANT_KEY_OLD_PRICE = "MERCHANT_KEY_OLD_PRICE";
    public static final String MERCHANT_KEY_CURRENT_PRICE = "MERCHANT_KEY_CURRENT_PRICE";

    //关键字列表
    public static final String SEARCH_KEY_WORD_LIST_CACHE = "SEARCH_KEY_WORD_LIST_CACHE";


    /**
     * auth-token->用户id
     */
    public static final String ADMIN_KEY_MEMBER_ID_AUTH_TOKEN_PREFIX = "ADMIN_KEY_MEMBER_ID_AUTH_TOKEN_PREFIX:";
    public static final String ADMIN_KEY_MEMBER_ID_TOKEN_PREFIX = "ADMIN_KEY_MEMBER_ID_TOKEN_PREFIX:";

    public static final String KEY_ADMIN_MEMBER_ID = "KEY_ADMIN_MEMBER_ID";
    public static final String KEY_ADMIN_MEMBER_USERNAME = "KEY_ADMIN_MEMBER_USERNAME";
    public static final String KEY_ADMIN_MEMBER_REALNAME = "KEY_ADMIN_MEMBER_REALNAME";
    public static final String KEY_ADMIN_MEMBER_GROUP_NAME = "KEY_ADMIN_MEMBER_GROUP_NAME";

    /**
     * group action key set集合
     */
    public static final String ADMIN_GROUP_ACTION_KEY_SET = "ADMIN_GROUP_ACTION_KEY_SET";

    /**
     * 每一个组对应的key前缀,这是集合型key
     */
    public static final String ADMIN_GROUP_ACTION_KEYSET_PREFIX = "ADMIN_GROUP_ACTION_KEYSET_PREFIX:";


    public static final String ARTICLE_CATEGORY_KEY_PREFIX = "ARTICLE_CATEGORY_KEY_PREFIX:";

    public static final String ARTICLE_JSON_CACHE = "ARTICLE_JSON_CACHE:";
    public static final String ARTICLE_LIST_JSON_CACHE = "ARTICLE_LIST_JSON_CACHE:";
    public static final String ARTICLE_RECOMMEND_JSON_CACHE = "ARTICLE_RECOMMEND_JSON_CACHE:";
    public static final String ARTICLE_DISCOVERY_CACHE = "ARTICLE_DISCOVERY_CACHE";
    public static final String ARTICLE_HOME_PAGE = "ARTICLE_HOME_PAGE";
    public static final String COMMENT_JSON_CACHE = "COMMENT_JSON_CACHE:";

    public static final String BAR_JSON_CACHE = "BAR_JSON_CACHE:";
    public static final String ACTIVITY_JSON_CACHE = "ACTIVITY_JSON_CACHE:";
    public static final String WINE_JSON_CACHE = "WINE_JSON_CACHE:";
    public static final String RECOMMEND_WINE_JSON_CACHE = "RECOMMEND_WINE_JSON_CACHE:";
    public static final String PRODUCT_JSON_CACHE = "PRODUCT_JSON_CACHE:";
    public static final String PRODUCT_BY_TAG_JSON_CACHE = "PRODUCT_BY_TAG_JSON_CACHE:";
    public static final String SHOPS_BY_TAG_JSON_CACHE = "SHOPS_BY_TAG_JSON_CACHE:";
    public static final String SPECIAL_TOPIC_JSON_CACHE = "SPECIAL_TOPIC_JSON_CACHE:";
    public static final String BRAND_JSON_CACHE = "BRAND_JSON_CACHE";
    public static final String FLASH_SALE_JSON_CACHE = "FLASH_SALE_JSON_CACHE";
    public static final String FLASH_SALE_CACHE_BY_PRODUCT_ID = "FLASH_SALE_CACHE_BY_PRODUCT_ID:";

    public static final String PARAM_KEY_SMS_NOTIFY_APP_KEY = "PARAM_KEY_SMS_NOTIFY_APP_KEY";
    public static final String PARAM_KEY_SMS_NOTIFY_API_SECRET = "PARAM_KEY_SMS_NOTIFY_API_SECRET";
    public static final String PARAM_KEY_SMS_BUSINESS_APP_KEY = "PARAM_KEY_SMS_BUSINESS_APP_KEY";
    public static final String PARAM_KEY_SMS_BUSINESS_API_SECRET = "PARAM_KEY_SMS_BUSINESS_API_SECRET";
    public static final String PARAM_KEY_SMS_REQUEST_URL = "PARAM_KEY_SMS_REQUEST_URL";

    public static final String PRODUCT_TAB_JSON_CACHE = "PRODUCT_TAB_JSON_CACHE:";
    public static final String PRODUCT_FAVOR_CACHE = "PRODUCT_FAVOR_CACHE:";
    public static final String DEFAULT_RECOMMEND_PRODUCT = "DEFAULT_RECOMMEND_PRODUCT:";
    public static final String RECOMMEND_PRODUCT_KEY = "RECOMMEND_PRODUCT_KEY:";
    public static final String RELATE_PRODUCT_KEY = "RELATE_PRODUCT_KEY:";

    public static final String KEY_WINE_ID = "KEY_WINE_ID";
    public static final String SELF_TAKE_LIST_CACHE = "SELF_TAKE_LIST_CACHE:";

    public static final String PRODUCT_SEARCH_KEYWORDS_JSON_CACHE = "PRODUCT_SEARCH_KEYWORDS_JSON_CACHE:";
    public static final String SEARCH_KEYWORDS_JSON_CACHE = "SEARCH_KEYWORDS_JSON_CACHE:";
    public static final String MINI_APP_ACCESS_TOKEN = "MINI_APP_ACCESS_TOKEN";
    public static final String PREFETCH_JSON_CACHE = "PREFETCH_JSON_CACHE:";
    public static final String CAROUSEL_JSON_CACHE = "CAROUSEL_JSON_CACHE:";
    public static final String FLASH_SALE_TODAY_JSON_CACHE = "FLASH_SALE_TODAY_JSON_CACHE:";
    public static final String SHOP_LIST_JSON_CACHE = "SHOP_LIST_JSON_CACHE:";
    public static final String TOP_SHOP_LIST_JSON_CACHE = "TOP_SHOP_LIST_JSON_CACHE:";
    public static final String KEY_MEMBER_SHIP_JSON_CACHE_KEY = "KEY_MEMBER_SHIP_JSON_CACHE_KEY:";
    public static final String PRODUCT_TAB_DETAIL_JSON_CACHE = "PRODUCT_TAB_DETAIL_JSON_CACHE:";


    public static final String PARAM_KEY_MIN_ORDER_MONEY_TO_GROUPON = "PARAM_KEY_MIN_ORDER_MONEY_TO_GROUPON";
    public static final String PARAM_KEY_GROUPON_INTERNAL = "PARAM_KEY_GROUPON_INTERNAL";
    public static final String PARAM_KEY_GROUPON_DISCOUNT = "PARAM_KEY_GROUPON_DISCOUNT";
    public static final String PARAM_KEY_TEAM_LEADER_AWARD_PERCENTAGE = "PARAM_KEY_TEAM_LEADER_AWARD_PERCENTAGE";
    public static final String PARAM_KEY_TEAM_INVITE_AWARD_PERCENTAGE = "PARAM_KEY_TEAM_INVITE_AWARD_PERCENTAGE";
    public static final String PARAM_KEY_GROUPON_REQUIRE_ORDERS = "PARAM_KEY_GROUPON_REQUIRE_ORDERS";
    public static final String PARAM_KEY_GROUPON_TRY_REQUIRE_ORDERS = "PARAM_KEY_GROUPON_TRY_REQUIRE_ORDERS";
    public static final String PARAM_KEY_GROUPON_TRY_HIT_COUNT = "PARAM_KEY_GROUPON_TRY_HIT_COUNT";
    public static final String PARAM_KEY_GROUPON_TIME_LIMIT_IN_SERCONDS = "PARAM_KEY_GROUPON_TIME_LIMIT_IN_SERCONDS";
    public static final String PARAM_KEY_GROUPON_SUPER_REQUIRE_ORDERS = "PARAM_KEY_GROUPON_SUPER_REQUIRE_ORDERS";
    public static final String PARAM_KEY_GROUPON_LOTTERY_REQUIRE_ORDERS = "PARAM_KEY_GROUPON_LOTTERY_REQUIRE_ORDERS";
    public static final String PARAM_KEY_GROUPON_LOTTERY_HIT_COUNT = "PARAM_KEY_GROUPON_LOTTERY_HIT_COUNT";
    public static final String PARAM_KEY_GROUPON_TIME_LIMIT_IN_SECONDS = "PARAM_KEY_GROUPON_TIME_LIMIT_IN_SECONDS";
    public static final String PARAM_KEY_GROUPON_TRY_TIME_LIMIT_IN_SECONDS = "PARAM_KEY_GROUPON_TRY_TIME_LIMIT_IN_SECONDS";
    public static final String PARAM_KEY_GROUPON_SUPER_TIME_LIMIT_IN_SECONDS = "PARAM_KEY_GROUPON_SUPER_TIME_LIMIT_IN_SECONDS";
    public static final String PARAM_KEY_GROUPON_LOTTERY_TIME_LIMIT_IN_SECONDS = "PARAM_KEY_GROUPON_LOTTERY_TIME_LIMIT_IN_SECONDS";

    public static final String GROUPON_PRODUCT_JSON_CACHE = "GROUPON_PRODUCT_JSON_CACHE:";

    public static final String KEY_LOGIN_MAX_ERROR_BY_ID = "KEY_LOGIN_MAX_ERROR_BY_ID:";
    public static final String PARAM_KEY_AWARD_INVITE = "PARAM_KEY_AWARD_INVITE";

    public static final String GROUP_PRODUCT_JSON_CACHE_KEY_PREFIX = "GROUP_PRODUCT_JSON_CACHE_KEY_PREFIX:";
}
