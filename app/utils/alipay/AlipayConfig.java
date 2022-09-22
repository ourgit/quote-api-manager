package utils.alipay;

public class AlipayConfig {

    public static String ALI_PAY_URL = "https://openapi.alipay.com/gateway.do";

    // 合作身份者ID，签约账号，以2088开头由16位纯数字组成的字符串，查看地址：https://b.alipay.com/order/pidAndKey.htm
    public static String sell_account_name = "";
    // 收款支付宝账号，以2088开头由16位纯数字组成的字符串，一般情况下收款账号就是签约账号
    // 支付宝的公钥,查看地址：https://b.alipay.com/order/pidAndKey.htm
    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 签名方式
    public static String sign_type = "RSA";
    // 支付类型 ，无需修改
    public static String payment_type = "1";
    // 调用的接口名，无需修改
    public static String service = "create_direct_pay_by_user";
    // 字符编码格式 目前支持 gbk 或 utf-8
    public static String input_charset = "utf-8";
//↑↑↑↑↑↑↑↑↑↑请在这里配置防钓鱼信息，如果没开通防钓鱼功能，为空即可 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

}

