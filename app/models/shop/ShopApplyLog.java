package models.shop;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlSerializer;

import javax.persistence.*;

@Entity
@Table(name = "v1_shop_apply_log")
public class ShopApplyLog extends Model {

    public static final int STATUS_AUDIT_DENY = -10;
    public static final int STATUS_TO_AUDIT = 10;
    public static final int STATUS_AUDIT_PASS = 20;
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "uid")
    public long uid;

    @Column(name = "shop_id")
    public long shopId;

    @Column(name = "shop_name")
    public String shopName;

    @Column(name = "user_name")
    public String userName;

    @Column(name = "audit_note")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String auditNote;


    @Column(name = "phone_number")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String phoneNumber;

    @Column(name = "address")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String address;

    @Column(name = "digest")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String digest;

    @Column(name = "business_items")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String businessItems;

    @Column(name = "auditor_uid")
    public long auditorUid;

    @Column(name = "auditor_name")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String auditorName;


    @Column(name = "logo")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String logo;

    @Column(name = "create_time")
    public long createTime;

    @Column(name = "audit_time")
    public long auditTime;

    @Column(name = "status")
    public int status;

    public static Finder<Long, ShopApplyLog> find = new Finder<>(ShopApplyLog.class);

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

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAuditNote() {
        return auditNote;
    }

    public void setAuditNote(String auditNote) {
        this.auditNote = auditNote;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getBusinessItems() {
        return businessItems;
    }

    public void setBusinessItems(String businessItems) {
        this.businessItems = businessItems;
    }

    public long getAuditorUid() {
        return auditorUid;
    }

    public void setAuditorUid(long auditorUid) {
        this.auditorUid = auditorUid;
    }

    public String getAuditorName() {
        return auditorName;
    }

    public void setAuditorName(String auditorName) {
        this.auditorName = auditorName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(long auditTime) {
        this.auditTime = auditTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
