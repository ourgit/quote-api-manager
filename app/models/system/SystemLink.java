package models.system;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlSerializer;

import javax.persistence.*;

/**
 * 友情链接表
 */
@Entity
@Table(name = "v1_system_link")
public class SystemLink extends Model {
    //1显示 2隐藏
    public static final int STATUS_SHOW = 1;
    public static final int STATUS_HIDE = 2;

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "name")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String name;

    @Column(name = "url")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String url;

    @Column(name = "sort")
    public int sort;

    @Column(name = "status")
    public int status;//1显示 2隐藏

    @Column(name = "note")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String description;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @Column(name = "update_time")
    public long updateTime;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @Column(name = "create_time")
    public long createdTime;


    public static Finder<Integer, SystemLink> find = new Finder<>(SystemLink.class);

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String toString() {
        return "SystemLink{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", sort=" + sort +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", updateTime=" + updateTime +
                ", createdTime=" + createdTime +
                '}';
    }
}
