package models.admin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlSerializer;

import javax.persistence.*;

/**
 * 群成员
 */
@Entity
@Table(name = "cp_group_user")
public class GroupUser extends Model {
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "group_id")
    public int groupId;

    @Column(name = "group_name")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String groupName;

    @Column(name = "member_id")
    public long memberId;

    @Column(name = "realname")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String realName;

    @Column(name = "create_time")
    public long createTime;

    public static Finder<Long, GroupUser> find = new Finder<>(GroupUser.class);

    public void setId(long id) {
        this.id = id;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "GroupUser{" +
                "id=" + id +
                ", groupId=" + groupId +
                ", groupName='" + groupName + '\'' +
                ", memberId=" + memberId +
                ", realName='" + realName + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
