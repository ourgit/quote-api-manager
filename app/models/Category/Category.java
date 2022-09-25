package models.Category;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlSerializer;

import javax.persistence.*;
import java.util.List;

/**
 * 商品分类
 */
@Entity
@Table(name = "v1_category")
public class Category extends Model {

    public static final int SHOW_CATEGORY = 1;
    public static final int HIDE_CATEGORY = 2;

    public static final int CATE_TYPE_POST = 1;
    public static final int CATE_TYPE_SCORE = 2;
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "parent_id")
    public long parentId;//父类目ID=0时，代表的是一级的类目

    @Column(name = "name")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String name;

    @Column(name = "img_url")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String imgUrl;

    @Column(name = "poster")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String poster;

    @Column(name = "path")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String path;

    @Column(name = "path_name")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String pathName;

    @Column(name = "pinyin_abbr")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String pinyinAbbr;

    @Column(name = "seo_keyword")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String seoKeyword;

    @Column(name = "seo_description")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String seoDescription;

    @Column(name = "is_shown")
    public int show;

    @Column(name = "cate_type")
    public int cateType;

    @Column(name = "sort")
    public int sort;

    @Column(name = "sold_amount")
    public long soldAmount;

    @Column(name = "posts")
    public long posts;

    @Column(name = "create_time")
    public long createTime;

    @Transient
    public List<Category> children;

    public static Finder<Long, Category> find = new Finder<>(Category.class);

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShow(int show) {
        this.show = show;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public void setSoldAmount(long soldAmount) {
        this.soldAmount = soldAmount;
    }

    public long getId() {
        return id;
    }

    public long getParentId() {
        return parentId;
    }

    public String getName() {
        return name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getPoster() {
        return poster;
    }

    public String getPath() {
        return path;
    }

    public int getShow() {
        return show;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public long getSoldAmount() {
        return soldAmount;
    }

    public long getCreateTime() {
        return createTime;
    }

    public List<Category> getChildren() {
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }

    public String getPinyinAbbr() {
        return pinyinAbbr;
    }

    public void setPinyinAbbr(String pinyinAbbr) {
        this.pinyinAbbr = pinyinAbbr;
    }

    public int getCateType() {
        return cateType;
    }

    public void setCateType(int cateType) {
        this.cateType = cateType;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public long getPosts() {
        return posts;
    }

    public void setPosts(long posts) {
        this.posts = posts;
    }

    public String getSeoKeyword() {
        return seoKeyword;
    }

    public void setSeoKeyword(String seoKeyword) {
        this.seoKeyword = seoKeyword;
    }

    public String getSeoDescription() {
        return seoDescription;
    }

    public void setSeoDescription(String seoDescription) {
        this.seoDescription = seoDescription;
    }
}
