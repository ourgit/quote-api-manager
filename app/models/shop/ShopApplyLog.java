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

    @Column(name = "name_in_law")
    public String nameInLaw;

    @Column(name = "id_card_id")
    public long idCardId;

    @Column(name = "license_no")
    public String licenseNo;

    @Column(name = "audit_note")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String auditNote;

    @Column(name = "id_card_front_url")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String idCardFrontUrl;

    @Column(name = "id_card_back_url")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String idCardBackUrl;

    @Column(name = "license_url")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String licenseUrl;

    @Column(name = "phone_number")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String phoneNumber;

    @Column(name = "address")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String address;

    @Column(name = "auditor_uid")
    public long auditorUid;

    @Column(name = "auditor_name")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String auditorName;

    @Column(name = "company_name")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String companyName;

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

    public String getNameInLaw() {
        return nameInLaw;
    }

    public void setNameInLaw(String nameInLaw) {
        this.nameInLaw = nameInLaw;
    }

    public long getIdCardId() {
        return idCardId;
    }

    public void setIdCardId(long idCardId) {
        this.idCardId = idCardId;
    }

    public String getAuditNote() {
        return auditNote;
    }

    public void setAuditNote(String auditNote) {
        this.auditNote = auditNote;
    }

    public String getIdCardFrontUrl() {
        return idCardFrontUrl;
    }

    public void setIdCardFrontUrl(String idCardFrontUrl) {
        this.idCardFrontUrl = idCardFrontUrl;
    }

    public String getIdCardBackUrl() {
        return idCardBackUrl;
    }

    public void setIdCardBackUrl(String idCardBackUrl) {
        this.idCardBackUrl = idCardBackUrl;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
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

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
