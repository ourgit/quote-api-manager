package utils;

/**
 * 余额参数辅助类
 */
public class BalanceParam {

    /**
     * 默认设为交易增加
     *
     * @Link 参考BusinessConstant
     */
    public int itemId;
    public int bizType;
    public long memberId;
    public long leftBalance;
    public long freezeBalance;
    public long totalBalance;
    public long changeAmount;
    public String orderNo;
    public int days;
    public int freezeStatus;
    public long operatorId;
    public long shopId;
    public String orderTransactionId;
    /**
     * 金额变动类型，默认设为 可用余额变动
     */
    public String desc;
    public String walletAddress = "";


    public BalanceParam(int itemId, long memberId, long leftBalance,
                        long freezeBalance, long totalBalance,
                        long changeAmount, String desc,
                        int days,
                        int bizType,
                        String orderNo, int freezeStatus, long operatorId, long shopId, String orderTransactionId) {
        this.itemId = itemId;
        this.memberId = memberId;
        this.leftBalance = leftBalance;
        this.freezeBalance = freezeBalance;
        this.totalBalance = totalBalance;
        this.changeAmount = changeAmount;
        this.desc = desc;
        this.bizType = bizType;
        this.days = days;
        this.orderNo = orderNo;
        this.freezeStatus = freezeStatus;
        this.operatorId = operatorId;
        this.shopId = shopId;
        this.orderTransactionId = orderTransactionId;
    }

    @Override
    public String toString() {
        return "BalanceParam{" +
                "itemId=" + itemId +
                ", bizType=" + bizType +
                ", memberId=" + memberId +
                ", leftBalance=" + leftBalance +
                ", freezeBalance=" + freezeBalance +
                ", totalBalance=" + totalBalance +
                ", changeAmount=" + changeAmount +
                ", orderNo='" + orderNo + '\'' +
                ", days=" + days +
                ", freezeStatus=" + freezeStatus +
                ", desc='" + desc + '\'' +
                ", walletAddress='" + walletAddress + '\'' +
                '}';
    }

    public static class Builder {
        private int innerBizType;
        private int innerItemId;
        private long innerMemberId;
        private long innerLeftBalance;
        private long innerFreezeBalance;
        private long innerTotalBalance;
        private long innerChangeAmount;
        private int innerDays;
        private String innerDesc;
        private String innerOrderNo;
        private int innerFreezeStatus;
        private long innerOperatorId;
        private long innerShopId;
        private String innerOrderTransactionId;

        public Builder() {
        }

        public Builder(int itemId, long memberId, long leftBalance,
                       long freezeBalance, long totalBalance, long changeAmount, String desc,
                       int innerDays,
                       int bizType,
                       String orderNo, int freezeStatus, long operatorId, long orgId, String orderTransactionId) {
            this.innerItemId = itemId;
            this.innerMemberId = memberId;
            this.innerLeftBalance = leftBalance;
            this.innerFreezeBalance = freezeBalance;
            this.innerTotalBalance = totalBalance;
            this.innerChangeAmount = changeAmount;
            this.innerDesc = desc;
            this.innerBizType = bizType;
            this.innerDays = innerDays;
            this.innerOrderNo = orderNo;
            this.innerFreezeStatus = freezeStatus;
            this.innerOperatorId = operatorId;
            this.innerShopId = orgId;
            this.innerOrderTransactionId = orderTransactionId;
        }

        public BalanceParam.Builder bizType(int bizType) {
            this.innerBizType = bizType;
            return this;
        }

        public BalanceParam.Builder itemId(int itemId) {
            this.innerItemId = itemId;
            return this;
        }

        public BalanceParam.Builder memberId(long memberId) {
            this.innerMemberId = memberId;
            return this;
        }

        public BalanceParam.Builder leftBalance(long leftBalance) {
            this.innerLeftBalance = leftBalance;
            return this;
        }

        public BalanceParam.Builder freezeBalance(long freezeBalance) {
            this.innerFreezeBalance = freezeBalance;
            return this;
        }

        public BalanceParam.Builder totalBalance(long totalBalance) {
            this.innerTotalBalance = totalBalance;
            return this;
        }

        public BalanceParam.Builder desc(String desc) {
            this.innerDesc = desc;
            return this;
        }

        public BalanceParam.Builder changeAmount(long changeAmount) {
            this.innerChangeAmount = changeAmount;
            return this;
        }

        public BalanceParam.Builder days(int days) {
            this.innerDays = days;
            return this;
        }

        public BalanceParam.Builder orderNo(String orderNo) {
            this.innerOrderNo = orderNo;
            return this;
        }

        public BalanceParam.Builder freezeStatus(int freezeStatus) {
            this.innerFreezeStatus = freezeStatus;
            return this;
        }

        public Builder operatorId(long operatorId) {
            this.innerOperatorId = operatorId;
            return this;
        }

        public Builder shopId(long shopId) {
            this.innerShopId = shopId;
            return this;
        }

        public Builder orderTransactionId(String orderTransactionId) {
            this.innerOrderTransactionId = orderTransactionId;
            return this;
        }

        public BalanceParam build() {
            return new BalanceParam(innerItemId, innerMemberId,
                    innerLeftBalance, innerFreezeBalance, innerTotalBalance, innerChangeAmount, innerDesc,
                    innerDays,
                    innerBizType,
                    innerOrderNo, innerFreezeStatus, innerOperatorId, innerShopId, innerOrderTransactionId);
        }

    }

}
