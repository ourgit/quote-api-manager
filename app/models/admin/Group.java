package models.admin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlSerializer;

import javax.persistence.*;

/**
 * 组
 */
@Entity
@Table(name = "cp_group")
public class Group extends Model {
    public static final int  DEFAULT_SYSTEM = 1;
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "name")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String groupName;

    @Column(name = "is_admin")
    public boolean isAdmin;

    @Column(name = "description")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String description;

    @Column(name = "create_time")
    public long createdTime;

    public static Finder<Integer, Group> find = new Finder<>(Group.class);

    public void setId(int id) {
        this.id = id;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", groupName='" + groupName + '\'' +
                ", isAdmin=" + isAdmin +
                ", description='" + description + '\'' +
                ", createdTime=" + createdTime +
                '}';
    }
}
