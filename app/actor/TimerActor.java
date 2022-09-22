package actor;

import akka.actor.AbstractLoggingActor;
import models.log.BalanceLog;
import models.msg.Msg;
import models.user.MemberBalance;
import utils.BalanceParam;
import utils.DateUtils;

import javax.inject.Inject;

/**
 * Created by win7 on 2016/7/14.
 */
public class TimerActor extends AbstractLoggingActor {

    @Inject
    DateUtils dateUtils;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ActorProtocol.BALANCE_LOG.class, param -> {
                    makeBalanceLog(param);
                })
                .build();
    }

    private void makeBalanceLog(ActorProtocol.BALANCE_LOG param) {
        MemberBalance balance = MemberBalance.find.query().where()
                .eq("uid", param.uid)
                .eq("itemId", param.itemId)
                .setMaxRows(1).findOne();
        long currentTime = dateUtils.getCurrentTimeBySecond();
        BalanceParam balanceParam = param.balanceParam;
        BalanceLog balanceLog = new BalanceLog();
        balanceLog.setUid(param.uid);
        balanceLog.setOrderNo(balanceParam.orderNo);
        balanceLog.setItemId(param.itemId);
        balanceLog.setLeftBalance(balance.leftBalance);
        balanceLog.setFreezeBalance(balance.freezeBalance);
        balanceLog.setTotalBalance(balance.totalBalance);
        balanceLog.setChangeAmount(balanceParam.changeAmount);
        balanceLog.setBizType(balanceParam.bizType);
        balanceLog.setNote(balanceParam.desc);
        balanceLog.setCreateTime(currentTime);
        balanceLog.save();

        Msg msg = new Msg();
        msg.setUid(balance.uid);
        msg.setTitle(balanceParam.desc);
        msg.setContent(balanceParam.desc);
        msg.setLinkUrl("");
        msg.setMsgType(Msg.MSG_TYPE_BALANCE);
        msg.setStatus(Msg.STATUS_NOT_READ);
        msg.setItemId(balance.itemId);
        msg.setChangeAmount(balanceParam.changeAmount);
        msg.setCreateTime(currentTime);
        msg.save();
    }
}
