package actor;

import utils.BalanceParam;

/**
 * Actor对象
 */
public class ActorProtocol {

    public static class BALANCE_LOG {
        public long uid;
        public int itemId;
        public BalanceParam balanceParam;

        public BALANCE_LOG(long uid, int itemId, BalanceParam balanceParam) {
            this.uid = uid;
            this.itemId = itemId;
            this.balanceParam = balanceParam;
        }
    }
}
