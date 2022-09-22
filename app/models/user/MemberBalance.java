package models.user;

import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;

/**
 * 用户余额类
 */
@Entity
@Table(name = "v1_member_balance")
public class MemberBalance extends Model {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    public long id;

    @Column(name = "uid")
    public long uid;//用户id

    @Column(name = "item_id")
    public int itemId;//业务ID

    @Column(name = "left_balance")
    public long leftBalance;

    @Column(name = "freeze_balance")
    public long freezeBalance;//冻结余额

    @Column(name = "total_balance")
    public long totalBalance;//全部余额

    @Column(name = "expire_time")
    public long expireTime;

    @Column(name = "update_time")
    public long updateTime;//更新时间

    @Column(name = "create_time")
    public long createdTime;//创建时间

    public static Finder<Long, MemberBalance> find = new Finder<>(MemberBalance.class);

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public double getLeftBalance() {
        return leftBalance;
    }

    public void setLeftBalance(long leftBalance) {
        this.leftBalance = leftBalance;
    }

    public double getFreezeBalance() {
        return freezeBalance;
    }

    public void setFreezeBalance(long freezeBalance) {
        this.freezeBalance = freezeBalance;
    }

    public double getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(long totalBalance) {
        this.totalBalance = totalBalance;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    @Override
    public String toString() {
        return "MemberBalance{" +
                "id=" + id +
                ", uid=" + uid +
                ", itemId=" + itemId +
                ", leftBalance=" + leftBalance +
                ", freezeBalance=" + freezeBalance +
                ", totalBalance=" + totalBalance +
                ", expireTime=" + expireTime +
                ", updateTime=" + updateTime +
                ", createdTime=" + createdTime +
                '}';
    }
}
