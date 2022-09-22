package models.admin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlSerializer;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 成员
 */
@Entity
@Table(name = "cp_member")
public class AdminMember extends Model {

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

    @Column(name = "follow_count")
    public long followCount;//关注数

    @Column(name = "fans_count")
    public long fansCount;//粉丝数

    @Column(name = "favs_count")
    public long favsCount;//收藏数

    @Column(name = "likes_count")
    public long likesCount;//赞数

    @Column(name = "award_percentage")
    public double awardPercentage;//返点比例

    @Column(name = "bg_img_url")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String bgImgUrl;

    @Transient
    public List<Integer> groupIdList = new ArrayList<>();

    @Transient
    public List<GroupUser> groupUserList = new ArrayList<>();

    @Transient
    public String groupName;


    public static Finder<Long, AdminMember> find = new Finder<>(AdminMember.class);

    public void setId(long id) {
        this.id = id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public void setLastLoginIP(String lastLoginIP) {
        this.lastLoginIP = lastLoginIP;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static void setFind(Finder<Long, AdminMember> find) {
        AdminMember.find = find;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getRealName() {
        return realName;
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

    public long getCreatedTime() {
        return createdTime;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public String getLastLoginIP() {
        return lastLoginIP;
    }

    public long getShopId() {
        return shopId;
    }

    public int getStatus() {
        return status;
    }

    public List<Integer> getGroupIdList() {
        return groupIdList;
    }

    public void setGroupIdList(List<Integer> groupIdList) {
        this.groupIdList = groupIdList;
    }

    public List<GroupUser> getGroupUserList() {
        return groupUserList;
    }

    public void setGroupUserList(List<GroupUser> groupUserList) {
        this.groupUserList = groupUserList;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPinyinAbbr() {
        return pinyinAbbr;
    }

    public void setPinyinAbbr(String pinyinAbbr) {
        this.pinyinAbbr = pinyinAbbr;
    }

    public long getFollowCount() {
        return followCount;
    }

    public void setFollowCount(long followCount) {
        this.followCount = followCount;
    }

    public long getFansCount() {
        return fansCount;
    }

    public void setFansCount(long fansCount) {
        this.fansCount = fansCount;
    }

    public long getFavsCount() {
        return favsCount;
    }

    public void setFavsCount(long favsCount) {
        this.favsCount = favsCount;
    }

    public long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(long likesCount) {
        this.likesCount = likesCount;
    }

    public String getBgImgUrl() {
        return bgImgUrl;
    }

    public void setBgImgUrl(String bgImgUrl) {
        this.bgImgUrl = bgImgUrl;
    }

    public double getAwardPercentage() {
        return awardPercentage;
    }

    public void setAwardPercentage(double awardPercentage) {
        this.awardPercentage = awardPercentage;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}
