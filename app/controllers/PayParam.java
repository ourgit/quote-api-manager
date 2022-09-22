package controllers;

/**
 * 支付参数辅助类
 */
public class PayParam {

    public String tradeNo;
    public String subject;
    public String productionCode;
    public long totalAmount;
    public long realPayMoney;
    public long returnMoney;
    public long uid;
    public String openId;
    public int payMethod;

    public PayParam(String tradeNo, String subject, String productionCode, long totalAmount,
                    long uid, String openId, long realPayMoney, int payMethod, long returnMoney) {
        this.tradeNo = tradeNo;
        this.subject = subject;
        this.productionCode = productionCode;
        this.totalAmount = totalAmount;
        this.uid = uid;
        this.openId = openId;
        this.realPayMoney = realPayMoney;
        this.returnMoney = returnMoney;
        this.payMethod = payMethod;
    }

    @Override
    public String toString() {
        return "PayParam{" +
                "tradeNo='" + tradeNo + '\'' +
                ", subject='" + subject + '\'' +
                ", productionCode='" + productionCode + '\'' +
                ", totalAmount=" + totalAmount +
                ", uid=" + uid +
                ", openId='" + openId + '\'' +
                '}';
    }

    public static class Builder {
        private String innerTradeNo;
        private String innerSubject;
        private String innerProductionCode;
        private long innerTotalAmount;
        private long innerUid;
        private String innerOpenId;
        private long innerRealPayMoney;
        private long innerGroupBuyLauncherId;
        private int innerPayMethod;
        private long innerReturnMoney;

        public Builder() {
        }

        public Builder(String innerTradeNo, String innerSubject, String innerProductionCode,
                       long innerTotalAmount, long innerUid, String innerOpenId,
                       long innerRealPayMoney, int innerPayMethod, long innerReturnMoney) {
            this.innerTradeNo = innerTradeNo;
            this.innerSubject = innerSubject;
            this.innerProductionCode = innerProductionCode;
            this.innerTotalAmount = innerTotalAmount;
            this.innerUid = innerUid;
            this.innerOpenId = innerOpenId;
            this.innerRealPayMoney = innerRealPayMoney;
            this.innerPayMethod = innerPayMethod;
            this.innerReturnMoney = innerReturnMoney;
        }

        public PayParam.Builder tradeNo(String tradeNo) {
            this.innerTradeNo = tradeNo;
            return this;
        }

        public PayParam.Builder subject(String subject) {
            this.innerSubject = subject;
            return this;
        }

        public PayParam.Builder productionCode(String productionCode) {
            this.innerProductionCode = productionCode;
            return this;
        }

        public PayParam.Builder totalAmount(long totalAmount) {
            this.innerTotalAmount = totalAmount;
            return this;
        }

        public PayParam.Builder uid(long uid) {
            this.innerUid = uid;
            return this;
        }

        public PayParam.Builder openId(String openId) {
            this.innerOpenId = openId;
            return this;
        }

        public PayParam.Builder payMethod(int payMethod) {
            this.innerPayMethod = payMethod;
            return this;
        }

        public PayParam.Builder realPayMoney(long realPayMoney) {
            this.innerRealPayMoney = realPayMoney;
            return this;
        }

        public PayParam.Builder returnMoney(long innerReturnMoney) {
            this.innerReturnMoney = innerReturnMoney;
            return this;
        }

        public PayParam build() {
            return new PayParam(innerTradeNo, innerSubject, innerProductionCode, innerTotalAmount, innerUid,
                    innerOpenId, innerRealPayMoney, innerPayMethod, innerReturnMoney);
        }
    }

}
