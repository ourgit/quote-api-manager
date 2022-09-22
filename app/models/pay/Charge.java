package models.pay;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.DateSerializer;
import myannotation.EscapeHtmlSerializer;

import javax.persistence.*;

/**
 * 充值
 */
@Entity
@Table(name = "v1_charge")
public class Charge extends Model {
    //1为未付款,2为已付款
    public static final int STATUS_UNPAY = 1;
    public static final int STATUS_PAYED = 2;

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "transaction_id")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String transactionId;

    @Column(name = "sub_id")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String subId;

    @Column(name = "uid")
    public long uid;

    @Column(name = "amount")
    public int amount;//金额统一以分为单位

    @Column(name = "status")
    public int status;

    @Column(name = "pay_type")
    public int payType;

    @Column(name = "update_time")
    @JsonSerialize(using = DateSerializer.class)
    public long updateTime;

    public static Finder<Long, Charge> find = new Finder<>(Charge.class);

    public void setId(long id) {
        this.id = id;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setSubId(String subId) {
        this.subId = subId;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Charge{" +
                "id=" + id +
                ", transactionId='" + transactionId + '\'' +
                ", subId='" + subId + '\'' +
                ", uid=" + uid +
                ", amount=" + amount +
                ", status=" + status +
                ", payType=" + payType +
                ", updateTime=" + updateTime +
                '}';
    }
}
