package models.stat;

import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;

/**
 * 统计总业绩
 */
@Entity
@Table(name = "v1_stat_overview_total")
public class StatTotalOverview extends Model {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "commission")
    public long commission;//抽成奖励

    @Column(name = "orders")
    public long orders;//订单数

    @Column(name = "order_amount")
    public long orderAmount;//订单总额

    @Column(name = "shop_id")
    public long shopId;

    @Column(name = "create_time")
    public long createdTime;

    public static Finder<Long, StatTotalOverview> find = new Finder<>(StatTotalOverview.class);

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCommission() {
        return commission;
    }

    public void setCommission(long commission) {
        this.commission = commission;
    }

    public long getOrders() {
        return orders;
    }

    public void setOrders(long orders) {
        this.orders = orders;
    }

    public long getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(long orderAmount) {
        this.orderAmount = orderAmount;
    }

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }
}
