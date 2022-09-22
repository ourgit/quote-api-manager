package models.upgrade;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.DateSerializer;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 灰度用户
 */
@Entity
@Table(name = "v1_upgrade_grey_user")
public class GreyUser extends Model implements Serializable {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "user_name")
    public String userName;

    @Column(name = "uid")
    public long uid;

    @Column(name = "note")
    public String note;//备注

    @Column(name = "update_time")
    @JsonSerialize(using = DateSerializer.class)
    public long updateTime;

    @Column(name = "create_time")
    @JsonSerialize(using = DateSerializer.class)
    public long createTime;

    public static Finder<Long, GreyUser> find = new Finder<>(GreyUser.class);

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

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
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

    @Override
    public String toString() {
        return "greyUser{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", uid=" + uid +
                ", note='" + note + '\'' +
                ", updateTime=" + updateTime +
                ", createTime=" + createTime +
                '}';
    }
}
