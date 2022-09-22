package models.admin;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlSerializer;

import javax.persistence.*;
import java.util.List;

/**
 * 动态菜单
 */
@Entity
@Table(name = "cp_menu")
public class Menu extends Model {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "sort")
    public int sort;//分类排序，倒序

    @Column(name = "parent_id")
    public int parentId;//父类目ID=0时，代表的是一级的类目

    @Column(name = "enable")
    public boolean enable;

    @Column(name = "hidden")
    public boolean hidden;

    @Column(name = "path")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String path = "";

    @Column(name = "name")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String name = "";//分类名称

    @Column(name = "component")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String component = "";

    @Column(name = "redirect")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String redirect = "";

    @Column(name = "title")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String title = "";

    @Column(name = "icon")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String icon = "";

    @Column(name = "no_cache")
    public boolean noCache;

    @Column(name = "relative_path")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String relativePath = "";

    @Column(name = "active_menu")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String activeMenu = "";

    @Column(name = "create_time")
    public long createTime;

    @Transient
    public List<Menu> children;

    @Transient
    public Meta meta;

    public static Finder<Integer, Menu> find = new Finder<>(Menu.class);

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isNoCache() {
        return noCache;
    }

    public void setNoCache(boolean noCache) {
        this.noCache = noCache;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public List<Menu> getChildren() {
        return children;
    }

    public void setChildren(List<Menu> children) {
        this.children = children;
    }


    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getActiveMenu() {
        return activeMenu;
    }

    public void setActiveMenu(String activeMenu) {
        this.activeMenu = activeMenu;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "id=" + id +
                ", sort=" + sort +
                ", parentId=" + parentId +
                ", enable=" + enable +
                ", hidden=" + hidden +
                ", path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", component='" + component + '\'' +
                ", redirect='" + redirect + '\'' +
                ", title='" + title + '\'' +
                ", icon='" + icon + '\'' +
                ", noCache=" + noCache +
                ", relativePath='" + relativePath + '\'' +
                ", createTime=" + createTime +
                ", children=" + children +
                ", meta=" + meta +
                '}';
    }
}
