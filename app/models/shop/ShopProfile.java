package models.shop;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlSerializer;

import javax.persistence.*;

@Entity
@Table(name = "v1_shop_profile")
public class ShopProfile extends Model {

    public static final int STATUS_DRAFT = 1;
    public static final int STATUS_SUBMIT_FILES = 10;
    //    public static final int STATUS_TO_AUDIT = 20;
    public static final int STATUS_AUDIT = 30;
    public static final int STATUS_PAID = 40;
    public static final int STATUS_PASS = 50;
    public static final int STATUS_DENY = -1;
    public static final int STATUS_LOCK = -2;

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "shop_id")
    public long shopId;

    @Column(name = "uid")
    public long uid;

    @Column(name = "shop_name")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String shopName;

    @Column(name = "company_name")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String companyName;

    @Column(name = "license_number")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String licenseNumber;//营业执照号

    @Column(name = "license_img")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String licenseImg;//营业执照图片

    @Column(name = "law_name")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String lawName;//联系人

    @Column(name = "law_contact_number")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String lawContactNumber;

    @Column(name = "id_no")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String idNo;

    @Column(name = "id_card_front")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String idCardFront;

    @Column(name = "id_card_back")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String idCardBack;

    @Column(name = "contact_number")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String contactNumber;//联系电话

    @Column(name = "contact_name")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String contactName;//联系人

    @Column(name = "contact_address")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String contactAddress;//联系地址

    @Column(name = "digest")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String digest;

    @Column(name = "qualifications")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String qualifications;

    @Column(name = "description")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String description;//备注

    @Column(name = "approve_note")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String approveNote;//审核说明

    @Column(name = "apply_categories")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String applyCategories;

    @Column(name = "apply_categories_name")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String applyCategoriesName;

    @Column(name = "open_bank")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String openBank;

    @Column(name = "open_user_name")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String openUserName;

    @Column(name = "open_account_name")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String openAccountName;

    @Column(name = "open_license_img")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String openLicenseImg;

    @Column(name = "update_time")
    public long updateTime;//更新时间

    @Column(name = "status")
    public int status;

    @Column(name = "rect_logo")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String rectLogo;

    @Column(name = "transfer_proof")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String transferProof;

    @Column(name = "create_time")
    public long createTime;//创建时间

    public static Finder<Long, ShopProfile> find = new Finder<>(ShopProfile.class);

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getLicenseImg() {
        return licenseImg;
    }

    public void setLicenseImg(String licenseImg) {
        this.licenseImg = licenseImg;
    }

    public String getLawName() {
        return lawName;
    }

    public void setLawName(String lawName) {
        this.lawName = lawName;
    }

    public String getLawContactNumber() {
        return lawContactNumber;
    }

    public void setLawContactNumber(String lawContactNumber) {
        this.lawContactNumber = lawContactNumber;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getIdCardFront() {
        return idCardFront;
    }

    public void setIdCardFront(String idCardFront) {
        this.idCardFront = idCardFront;
    }

    public String getIdCardBack() {
        return idCardBack;
    }

    public void setIdCardBack(String idCardBack) {
        this.idCardBack = idCardBack;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getQualifications() {
        return qualifications;
    }

    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApproveNote() {
        return approveNote;
    }

    public void setApproveNote(String approveNote) {
        this.approveNote = approveNote;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getApplyCategories() {
        return applyCategories;
    }

    public void setApplyCategories(String applyCategories) {
        this.applyCategories = applyCategories;
    }

    public String getApplyCategoriesName() {
        return applyCategoriesName;
    }

    public void setApplyCategoriesName(String applyCategoriesName) {
        this.applyCategoriesName = applyCategoriesName;
    }

    public String getOpenBank() {
        return openBank;
    }

    public void setOpenBank(String openBank) {
        this.openBank = openBank;
    }

    public String getOpenUserName() {
        return openUserName;
    }

    public void setOpenUserName(String openUserName) {
        this.openUserName = openUserName;
    }

    public String getOpenAccountName() {
        return openAccountName;
    }

    public void setOpenAccountName(String openAccountName) {
        this.openAccountName = openAccountName;
    }

    public String getOpenLicenseImg() {
        return openLicenseImg;
    }

    public void setOpenLicenseImg(String openLicenseImg) {
        this.openLicenseImg = openLicenseImg;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRectLogo() {
        return rectLogo;
    }

    public void setRectLogo(String rectLogo) {
        this.rectLogo = rectLogo;
    }

    public String getTransferProof() {
        return transferProof;
    }

    public void setTransferProof(String transferProof) {
        this.transferProof = transferProof;
    }
}
