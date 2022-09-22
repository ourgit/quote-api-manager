package models.upgrade;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.DateSerializer;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 升级配置
 */
@Entity
@Table(name = "v1_upgrade_config")
public class UpgradeConfig extends Model implements Serializable {

    public static final int VERSION_STABLE = 1;
    public static final int VERSION_GREY = 2;

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "name")
    public String name;

    @Column(name = "version_number")
    public long versionNumber;

    @Column(name = "enable")
    public boolean enable;

    @Column(name = "force_update_rule")
    public String forceUpdateRule;

    @Column(name = "normal_update_rule")
    public String normalUpdateRule;

    @Column(name = "update_url")
    public String updateUrl;

    @Column(name = "update_type")
    public int updateType;

    @Column(name = "tip_type")
    public int tipType = 1;

    @Column(name = "version_type")
    public int versionType;//1正式版本，2灰度版本

    @Column(name = "platform")
    public String platform;

    @Column(name = "note")
    public String note;//备注

    @Column(name = "update_time")
    @JsonSerialize(using = DateSerializer.class)
    public long updateTime;

    @Column(name = "create_time")
    @JsonSerialize(using = DateSerializer.class)
    public long createTime;


    public static Finder<Long, UpgradeConfig> find = new Finder<>(UpgradeConfig.class);

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(long versionNumber) {
        this.versionNumber = versionNumber;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getForceUpdateRule() {
        return forceUpdateRule;
    }

    public void setForceUpdateRule(String forceUpdateRule) {
        this.forceUpdateRule = forceUpdateRule;
    }

    public String getNormalUpdateRule() {
        return normalUpdateRule;
    }

    public void setNormalUpdateRule(String normalUpdateRule) {
        this.normalUpdateRule = normalUpdateRule;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    public int getVersionType() {
        return versionType;
    }

    public void setVersionType(int versionType) {
        this.versionType = versionType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public int getUpdateType() {
        return updateType;
    }

    public void setUpdateType(int updateType) {
        this.updateType = updateType;
    }

    public int getTipType() {
        return tipType;
    }

    public void setTipType(int tipType) {
        this.tipType = tipType;
    }

    @Override
    public String toString() {
        return "UpgradeConfig{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", versionNumber=" + versionNumber +
                ", enable=" + enable +
                ", forceUpdateRule='" + forceUpdateRule + '\'' +
                ", normalUpdateRule='" + normalUpdateRule + '\'' +
                ", updateUrl='" + updateUrl + '\'' +
                ", updateType=" + updateType +
                ", tipType=" + tipType +
                ", versionType=" + versionType +
                ", platform='" + platform + '\'' +
                ", note='" + note + '\'' +
                ", updateTime=" + updateTime +
                ", createTime=" + createTime +
                '}';
    }
}
