package models.admin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlSerializer;

import javax.persistence.*;

@Entity
@Table(name = "v1_shop_admin")
public class ShopAdmin extends Model {

    public static final int STATUS_TO_AUDIT = -1;
    public static final int STATUS_AUDIT_DENY = -2;
    public static final int STATUS_NORMAL = 1;
    public static final int STATUS_LOCK = 2;

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "username")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String userName;

    @Column(name = "realname")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String realName;

    @Column(name = "avatar")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String avatar;

    @Column(name = "password")
    @JsonIgnore
    public String password;

    @Column(name = "create_time")
    public long createdTime;

    @Column(name = "last_time")
    public long lastLoginTime;

    @Column(name = "last_ip")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String lastLoginIP;

    @Column(name = "phone_number")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String phoneNumber;

    @Column(name = "is_admin")
    public boolean isAdmin;

    @Column(name = "shop_id")
    public long shopId;

    @Column(name = "shop_name")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String shopName;

    @Column(name = "pinyin_abbr")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String pinyinAbbr;

    @Column(name = "status")
    public int status;

    @Column(name = "bg_img_url")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String bgImgUrl;

    public static Finder<Long, ShopAdmin> find = new Finder<>(ShopAdmin.class);

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLastLoginIP() {
        return lastLoginIP;
    }

    public void setLastLoginIP(String lastLoginIP) {
        this.lastLoginIP = lastLoginIP;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
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

    public String getPinyinAbbr() {
        return pinyinAbbr;
    }

    public void setPinyinAbbr(String pinyinAbbr) {
        this.pinyinAbbr = pinyinAbbr;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getBgImgUrl() {
        return bgImgUrl;
    }

    public void setBgImgUrl(String bgImgUrl) {
        this.bgImgUrl = bgImgUrl;
    }
}
