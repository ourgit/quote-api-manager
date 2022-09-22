package models.article;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlSerializer;

import javax.persistence.*;

/**
 * 文章分类表
 */
@Entity
@Table(name = "v1_article_category")
public class ArticleCategory extends Model {

    //1显示 2隐藏
    public static final int SHOW = 1;
    public static final int HIDE = 2;

    public static final int TYPE_NEWS = 10;
    public static final int TYPE_BUSINESS = 20;
    public static final int TYPE_DISCOVER = 30;
    public static final int TYPE_DECORATE_TUTORIAL = 40;
    public static final int TYPE_DECORATE_SELECT = 50 ;

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    private String name;//分类名称

    @Column(name = "sort")
    private int sort;//分类排序，倒序

    @Column(name = "status")
    private int status;//1显示 2隐藏

    @Column(name = "cate_type")
    private int categoryType;

    @Column(name = "note")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    private String note;//备注

    @Column(name = "head_pic")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    private String headPic;

    @Column(name = "icon")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    private String icon;

    @Column(name = "display_mode")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    private String displayMode;

    @Column(name = "update_time")
    private long updateTime;

    @Column(name = "create_time")
    private long createdTime;

    public static Finder<Integer, ArticleCategory> find = new Finder<>(ArticleCategory.class);

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

    public int getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(int categoryType) {
        this.categoryType = categoryType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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

    public String getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(String displayMode) {
        this.displayMode = displayMode;
    }

    @Override
    public String toString() {
        return "ArticleCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sort=" + sort +
                ", status=" + status +
                ", categoryType=" + categoryType +
                ", note='" + note + '\'' +
                ", headPic='" + headPic + '\'' +
                ", icon='" + icon + '\'' +
                ", updateTime=" + updateTime +
                ", createdTime=" + createdTime +
                '}';
    }
}
