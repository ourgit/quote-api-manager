package models.admin;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlAuthoritySerializer;

import javax.persistence.*;

/**
 * 动作控制器的action
 */
@Entity
@Table(name = "cp_system_action")
public class Action  extends Model {
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public String id;//由action生成md5，唯一标识符


    @Column(name = "action_name")
    @JsonDeserialize(using = EscapeHtmlAuthoritySerializer.class)
    public String actionName;//权限方法

    @Column(name = "action_desc")
    @JsonDeserialize(using = EscapeHtmlAuthoritySerializer.class)
    public String actionDesc;//权限名称

    @Column(name = "module_name")
    @JsonDeserialize(using = EscapeHtmlAuthoritySerializer.class)
    public String moduleName;//模块控制器名称

    @Column(name = "module_desc")
    @JsonDeserialize(using = EscapeHtmlAuthoritySerializer.class)
    public String moduleDesc;//权限名称描述

    @Column(name = "need_show")
    public boolean needShow;//模块是否显示

    @Column(name = "display_order")
    public int sortValue;//排序

    @Column(name = "create_time")
    public long createdTime;

    public static Finder<String, Action> find = new Finder<>(Action.class);

    public void setId(String id) {
        this.id = id;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public void setActionDesc(String actionDesc) {
        this.actionDesc = actionDesc;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setModuleDesc(String moduleDesc) {
        this.moduleDesc = moduleDesc;
    }

    public void setNeedShow(boolean needShow) {
        this.needShow = needShow;
    }

    public void setSortValue(int sortValue) {
        this.sortValue = sortValue;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String toString() {
        return "Action{" +
                "id='" + id + '\'' +
                ", actionName='" + actionName + '\'' +
                ", actionDesc='" + actionDesc + '\'' +
                ", moduleName='" + moduleName + '\'' +
                ", moduleDesc='" + moduleDesc + '\'' +
                ", needShow=" + needShow +
                ", sortValue=" + sortValue +
                ", createdTime=" + createdTime +
                '}';
    }
}
