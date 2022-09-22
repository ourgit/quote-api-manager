package models.stat;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlSerializer;

import javax.persistence.*;

/**
 * 平台月业绩
 */
@Entity
@Table(name = "v1_stat_platform_month")
public class StatPlatformMonthSalesOverview extends Model {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "month")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String month;

    @Column(name = "orders")
    public long orders;//订单数

    @Column(name = "total_money")
    public long totalMoney;//订单总额

    @Column(name = "total_commission")
    public long totalCommission;//总抽成

    @Column(name = "platform_commission")
    public long platFormCommission;//平台抽成

    @Column(name = "products")
    public long products;//商品数

    @Column(name = "reg_count")
    public long regCount;

    @Column(name = "product_views")
    public long productViews;

    @Column(name = "visit_users")
    public long visitUsers;

    @Transient
    public String platformCommissonStr;
    @Transient
    public String totalMoneyStr;
    @Transient
    public String totalCommissionStr;

    @Transient
    public String totalCostStr;

    @Column(name = "shop_id")
    public long shopId;

    @Column(name = "create_time")
    public long createdTime;

    public static Finder<Long, StatPlatformMonthSalesOverview> find = new Finder<>(StatPlatformMonthSalesOverview.class);

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public long getOrders() {
        return orders;
    }

    public void setOrders(long orders) {
        this.orders = orders;
    }

    public long getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(long totalMoney) {
        this.totalMoney = totalMoney;
    }

    public long getTotalCommission() {
        return totalCommission;
    }

    public void setTotalCommission(long totalCommission) {
        this.totalCommission = totalCommission;
    }

    public long getPlatFormCommission() {
        return platFormCommission;
    }

    public void setPlatFormCommission(long platFormCommission) {
        this.platFormCommission = platFormCommission;
    }

    public long getProducts() {
        return products;
    }

    public void setProducts(long products) {
        this.products = products;
    }

    public long getRegCount() {
        return regCount;
    }

    public void setRegCount(long regCount) {
        this.regCount = regCount;
    }

    public String getPlatformCommissonStr() {
        return platformCommissonStr;
    }

    public void setPlatformCommissonStr(String platformCommissonStr) {
        this.platformCommissonStr = platformCommissonStr;
    }

    public String getTotalMoneyStr() {
        return totalMoneyStr;
    }

    public void setTotalMoneyStr(String totalMoneyStr) {
        this.totalMoneyStr = totalMoneyStr;
    }

    public String getTotalCommissionStr() {
        return totalCommissionStr;
    }

    public void setTotalCommissionStr(String totalCommissionStr) {
        this.totalCommissionStr = totalCommissionStr;
    }

    public String getTotalCostStr() {
        return totalCostStr;
    }

    public void setTotalCostStr(String totalCostStr) {
        this.totalCostStr = totalCostStr;
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
