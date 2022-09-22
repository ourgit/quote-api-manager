package utils.wechatpay;

/**
 * Created by Administrator on 2017/3/17.
 */
public class WechatConfig {
    public static final String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    public static final String QUERY_ORDER_URL = "https://api.mch.weixin.qq.com/pay/orderquery";
    public static final String REFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";
    public static final String WECHAT_PAY_NOTIFY_URL = "/v1/o/wechat/pay_notify/";
    public static final String MENU_CREATE_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
    public static final String MENU_GET_URL = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN";
    public static final String TEMPLATE_MSG_ID_GROUPBUY_SUCCEED = "aMB1aeAgddDsiuyH9kkXBdZHoNiWMgYgSt4JjFywgLY";
    public static final String TEMPLATE_MSG_ID_MERCHANT_BUY_SUCCEED = "RBGtbl9isI8zAvTrYg7NqwlLtyZVoVb_o3oPAPM6-HE";
    public static final String TEMPLATE_MSG_ID_MERCHANT_MAILED = "_BloxbW6DUpgSx1xBJsKJLwrhxd6FGNlPrM3gOV4_OE";
    public static final String TEMPLATE_MSG_ID_GROUPBUY_FAILED = "nOTGbUlg_VNpHabturzHN1tBWT3Lf_oejq9-NZmFmG8";
    public static final String TEMPLATE_MSG_ID_BUY_SUCCEED = "DwpCImUxXLmGhcLMhDEloyzvecL5PJUjWn_LgmPq_PE";

    public static final String SEND_TEMPLATE_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=ACCESS_TOKEN";
    public static final String TEMPLATE_MSG_ID_NEW_PROMOTION = "3FJxyF_48UkGxJf_f_ENKVpkWu-Trz7vY-cZarP1BBQ";

    public static final String WX_BAR_CODE_API_URL = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=ACCESS_TOKEN";
    public static final String WECHAT_TRANSFER = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";

}
