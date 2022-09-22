package models.admin;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlSerializer;

import javax.persistence.*;

/**
 * action分组
 */
@Entity
@Table(name = "cp_group_action")
public class GroupAction extends Model {
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "group_id")
    public int groupId;

    @Column(name = "system_action_id")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String actionId;


    public static Finder<Integer, GroupAction> find = new Finder<>(GroupAction.class);

    public void setId(int id) {
        this.id = id;
    }


    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    @Override
    public String toString() {
        return "GroupAction{" +
                "id=" + id +
                ", groupId=" + groupId +
                ", actionId='" + actionId + '\'' +
                '}';
    }
}
