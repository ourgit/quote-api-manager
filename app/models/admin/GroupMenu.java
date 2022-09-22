package models.admin;


import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;

/**
 * 动态菜单
 */
@Entity
@Table(name = "cp_group_menu")
public class GroupMenu extends Model {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "menu_id")
    public int menuId;

    @Column(name = "group_id")
    public int groupId;

    public static Finder<Integer, GroupMenu> find = new Finder<>(GroupMenu.class);

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "GroupMenu{" +
                "id=" + id +
                ", menuId=" + menuId +
                ", groupId=" + groupId +
                '}';
    }
}
