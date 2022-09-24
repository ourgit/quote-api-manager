package utils;

import constants.BusinessConstant;
import constants.RedisKeyConstant;
import models.post.Category;
import models.system.ParamConfig;
import play.Logger;
import play.cache.AsyncCacheApi;
import play.cache.NamedCache;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

import static constants.RedisKeyConstant.*;

/**
 * cache utils
 */
@Singleton
public class CacheUtils {
    Logger.ALogger logger = Logger.of(CacheUtils.class);

    @Inject
    EncodeUtils encodeUtils;

    @Inject
    @NamedCache("redis")
    protected AsyncCacheApi redis;

    /**
     * 获取用户表的cache key
     *
     * @param id
     * @return
     */
    public String getMemberKey(long id) {
        return RedisKeyConstant.KEY_MEMBER_ID + id;
    }

    public String getMemberKey(String id) {
        return RedisKeyConstant.KEY_MEMBER_ID + id;
    }

    public String getMemberKey(int deviceType, String token) {
        return RedisKeyConstant.KEY_MEMBER_ID + deviceType + ":" + token;
    }

    /**
     * 获取手机号短信限制缓存key
     *
     * @param phoneNumber
     * @return
     */
    public String getSmsPhoneNumberLimitKey(String phoneNumber) {
        return SMS_PHONENUMBER_LIMIT_PREFIX_KEY + phoneNumber;
    }


    public String getSMSLastVerifyCodeKey(String phoneNumber) {
        return BusinessConstant.KEY_LAST_VERIFICATION_CODE_PREFIX_KEY + phoneNumber;
    }

    public String getAllLevelKeySet() {
        return ALL_LEVELS_KEY_SET;
    }

    public String getEachLevelKey(int level) {
        return LEVEL_KEY_PREFIX + level;
    }

    public String getAllScoreConfigKeySet() {
        return ALL_SCORE_CONFIGS_KEY_SET;
    }

    public String getEachScoreConfigKey(int type) {
        return SCORE_CONFIG_KEY_PREFIX + type;
    }

    public String getSoldAmountCacheKey(long merchantId) {
        return RedisKeyConstant.MERCHANTS_SOLD_AMOUNT_CACHE_KEY + merchantId;
    }

    /**
     * 商品分类列表集合
     *
     * @return
     */
    public String getCategoryPrefix(long categoryId) {
        return RedisKeyConstant.MERCHANTS_CATEGORIES_EACH_PREFIX + categoryId;
    }

    public String getWineCategoryPrefix(long categoryId) {
        return RedisKeyConstant.WINE_CATEGORIES_EACH_PREFIX + categoryId;
    }

    /**
     * 兑换分类缓存列表
     *
     * @return
     */
    public String getCategoryEachListKey(String filter) {
        return RedisKeyConstant.MERCHANTS_CATEGORIES_LIST_CACHE_KEY_PREFIX + filter;
    }

    /**
     * 品牌集合
     *
     * @return
     */
    public String getBrandKeySet() {
        return RedisKeyConstant.BRAND_KEY_SET;
    }

    /**
     * 单个品牌的KEY
     *
     * @param brandId
     * @return
     */
    public String getBrandKey(long brandId) {
        return RedisKeyConstant.BRAND_EACH_KEY + brandId;
    }

    /**
     * 首页商品图缓存
     *
     * @return
     */
    public String homepageBrandJsonCache() {
        return HOMEPAGE_BRAND_JSON_CACHE;
    }

    public String homepageOnArrivalJsonCache() {
        return ON_NEW_ARRIVAL_JSON_CACHE;
    }

    public String homepageOnPromotionJsonCache() {
        return ON_PROMOTION_JSON_CACHE;
    }

    public String getMailFeeCacheKeyList() {
        return RedisKeyConstant.MAIL_FEE_CACHE_KEYSET;
    }

    public String getMailFeeKey(String regionCode) {
        return RedisKeyConstant.MAIL_FEE_CACHE_KEY_PREFIX + regionCode;
    }


    public String getCouponConfigCacheKeyset() {
        return RedisKeyConstant.COUPON_CONFIG_KEYSET;
    }

    public String getCouponConfigCacheKey(long id) {
        return RedisKeyConstant.COUPON_CONFIG_KEY_PREFIX + id;
    }

    public String getParamConfigCacheKeyset() {
        return RedisKeyConstant.PARAM_CONFIG_KEYSET;
    }

//    public String getParamConfigCacheKey() {
//        return RedisKeyConstant.PARAM_CONFIG_KEY_PREFIX;
//    }

    public String getMerchantCacheKey(long id) {
        return RedisKeyConstant.MERCHANT_CACHE_KEY_PREFIX + id;
    }

    public String getProductJsonCacheKey(long id) {
        return RedisKeyConstant.MERCHANT_JSON_CACHE_KEY_PREFIX + id;
    }

    public String getMerchantCacheKeySet() {
        return RedisKeyConstant.MERCHANT_CACHE_KEYSET;
    }



    public String getMemberTokenKey(int deviceType, long uid) {
        return RedisKeyConstant.KEY_MEMBER_TOKEN_KEY + deviceType + ":" + uid;
    }


    /**
     * 获取组缓存的key
     *
     * @param groupId
     * @return
     */
    public String getGroupActionKey(int groupId) {
        return ADMIN_GROUP_ACTION_KEYSET_PREFIX + groupId;
    }

    private void clearHomepageJsonCache() {
        String categoryJsonCachePlatform = getCategoryJsonCache(Category.CATE_TYPE_POST);
        redis.remove(categoryJsonCachePlatform);
        String categoryJsonCacheScore = getCategoryJsonCache(Category.CATE_TYPE_SCORE);
        redis.remove(categoryJsonCacheScore);
        String key = homepageBrandJsonCache();
        redis.remove(key);
        redis.remove(RedisKeyConstant.KEY_ARTICLE_FOR_NEWBIE);
        redis.remove(RedisKeyConstant.KEY_HOME_PAGE_BOTTOM_INFO_LINKS);
        redis.remove(RedisKeyConstant.KEY_HOME_PAGE_INFO_LINKS);
        redis.remove(RedisKeyConstant.ON_NEW_ARRIVAL_JSON_CACHE);
        redis.remove(RedisKeyConstant.ON_PROMOTION_JSON_CACHE);
        Optional<List<String>> optional = redis.sync().get(SEARCH_JSON_CACHE_ALL_LIST);
        if (optional.isPresent()) {
            List<String> cacheList = optional.get();
            if (null != cacheList) {
                cacheList.forEach((eachKey) -> redis.remove(eachKey));
                cacheList.clear();
            }
        }
        redis.remove(SEARCH_JSON_CACHE_ALL_LIST);

        Optional<List<String>> optional2 = redis.sync().get(MERCHANTS_CATEGORIES_JSON_CACHE_LIST);
        if (optional2.isPresent()) {
            List<String> jsonCacheList = optional2.get();
            if (null != jsonCacheList) {
                jsonCacheList.forEach((eachKey) -> redis.remove(eachKey));
                jsonCacheList.clear();
            }
        }
        redis.remove(MERCHANTS_CATEGORIES_JSON_CACHE_LIST);
    }

    public String getCategoryJsonCache(int cateType) {
        return RedisKeyConstant.MERCHANTS_CATEGORIES_LIST_CACHE_KEY_PREFIX + cateType;
    }

    public String getShopCategoryJsonCache(long shopId) {
        return RedisKeyConstant.SHOP_PRODUCT_CATEGORIES_LIST_CACHE_KEY_PREFIX;
    }

    public void updateParamConfigCache() {
        logger.info("updateParamConfigCache");
        List<ParamConfig> list = ParamConfig.find.query().where().orderBy().desc("id").findList();
        list.forEach((config) -> {
            if (config.isEncrypt) {
                redis.set(config.key, encodeUtils.decrypt(config.value));
            } else redis.set(config.key, config.value);
        });
    }

    public void updateMerchantCategoryCache() {

        List<Category> list = Category.find.query().where().orderBy().desc("id").findList();
        list.forEach((each) -> {
            String key = getCategoryPrefix(each.id);
            redis.set(key, each);
        });
        clearHomepageJsonCache();
    }

    public void updateShopProductCategoryCache(long shopId) {
        String key = getShopCategoryJsonCache(shopId);
        redis.remove(key);
    }

    public void deleteArticleCache() {
        String key = getCarouselJsonCache();
        redis.remove(key);
        redis.remove(RedisKeyConstant.KEY_HOME_PAGE_BOTTOM_INFO_LINKS);
        redis.remove(RedisKeyConstant.KEY_HOME_PAGE_INFO_LINKS);
        redis.remove("KEY_CAROUSEL_PREFIX:0");
        redis.remove("KEY_CAROUSEL_PREFIX:1");
        redis.remove("KEY_CAROUSEL_PREFIX:2");
        redis.remove("KEY_CAROUSEL_PREFIX:3");
        redis.remove("KEY_CAROUSEL_PREFIX:4");
        redis.remove("KEY_CAROUSEL_PREFIX:5");
    }

    public String getMixOptionCacheSet() {
        return RedisKeyConstant.MIX_OPTION_KEY_SET;
    }

    public String getProductClassifyCacheSet() {
        return RedisKeyConstant.PRODUCT_CLASSIFY_KEY_SET;
    }

    public String getClassifyJsonCache(String classifyCode) {
        return RedisKeyConstant.PRODUCT_CLASSIFY_JSON_CACHE + classifyCode;
    }

    public String getClassifyJsonCache(long classifyId, int page) {
        return RedisKeyConstant.PRODUCT_CLASSIFY_BY_ID_JSON_CACHE + classifyId + ":" + page;
    }

    public String getClassifyListJsonCache() {
        return RedisKeyConstant.PRODUCT_CLASSIFY_LIST_JSON_CACHE;
    }

    public String getArticleCategoryKey(String cateName) {
        return RedisKeyConstant.ARTICLE_CATEGORY_KEY_PREFIX + cateName;
    }

    public String getProductsJsonCache() {
        return PRODUCT_JSON_CACHE;
    }

    public String getProductsByTagJsonCache(String tag) {
        return PRODUCT_BY_TAG_JSON_CACHE + tag;
    }

    public String getShopsByTagJsonCache(String tag) {
        return SHOPS_BY_TAG_JSON_CACHE + tag;
    }

    public String getSpecialTopicJsonCache() {
        return SPECIAL_TOPIC_JSON_CACHE;
    }

    public String getBrandJsonCache() {
        return BRAND_JSON_CACHE;
    }

    public String getFlashsaleJsonCache() {
        return FLASH_SALE_JSON_CACHE;
    }

    public String getFlashsaleCacheByProductId(long productId) {
        return FLASH_SALE_CACHE_BY_PRODUCT_ID + productId;
    }


    public String getProductTabJsonCache() {
        return PRODUCT_TAB_JSON_CACHE;
    }

    public String getFavorCache(long favorId) {
        return PRODUCT_FAVOR_CACHE + favorId;
    }

    public String defaultRecommendProductList() {
        return DEFAULT_RECOMMEND_PRODUCT;
    }

    public String getRecommendProductList(long productId) {
        return RECOMMEND_PRODUCT_KEY + productId;
    }

    public String getRelateProductList(long productId) {
        return RELATE_PRODUCT_KEY + productId;
    }

    public String getWineKey(long id) {
        return RedisKeyConstant.KEY_WINE_ID + id;
    }

    public String getProductKeywordsJsonCache() {
        return PRODUCT_SEARCH_KEYWORDS_JSON_CACHE;
    }

    public String getSearchKeywordsJsonCache() {
        return SEARCH_KEYWORDS_JSON_CACHE;
    }

    public String getMemberPlatformTokenKey(long uid) {
        return RedisKeyConstant.KEY_MEMBER_PLATFORM_TOKEN_KEY + uid;
    }

    public String getPrefetchJsonCache() {
        return PREFETCH_JSON_CACHE;
    }

    public String getCarouselJsonCache() {
        return RedisKeyConstant.CAROUSEL_JSON_CACHE;
    }

    public String getFlashsaleTodayJsonCache(int page) {
        return FLASH_SALE_TODAY_JSON_CACHE + page;
    }

    public String getShopListJsonCache(int page) {
        return SHOP_LIST_JSON_CACHE + page;
    }

    public String getTopShopListJsonCache() {
        return TOP_SHOP_LIST_JSON_CACHE;
    }

    public String getMembershipJsonCache() {
        return RedisKeyConstant.KEY_MEMBER_SHIP_JSON_CACHE_KEY;
    }

    public String getProductTabDetail(long tabId, int page) {
        return RedisKeyConstant.PRODUCT_TAB_DETAIL_JSON_CACHE + tabId + ":" + page;
    }

}
