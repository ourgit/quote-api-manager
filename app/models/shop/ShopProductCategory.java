package models.shop;

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
@Table(name = "v1_shop_category")
public class ShopProductCategory extends Model {

    public static final int SHOW_CATEGORY = 1;
    public static final int HIDE_CATEGORY = 2;
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "shop_id")
    public long shopId;//父类目ID=0时，代表的是一级的类目

    @Column(name = "parent_id")
    public long parentId;//父类目ID=0时，代表的是一级的类目

    @Column(name = "name")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String name;

    @Column(name = "path_name")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String pathName;

    @Column(name = "img_url")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String imgUrl;

    @Column(name = "poster")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String poster;

    @Column(name = "path")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String path;

    @Column(name = "pinyin_abbr")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String pinyinAbbr;

    @Column(name = "is_shown")
    public int show;

    @Column(name = "sort")
    public int sort;

    @Column(name = "sold_amount")
    public long soldAmount;

    @Column(name = "create_time")
    public long createTime;

    @Transient
    public List<ShopProductCategory> children;

    public static Finder<Long, ShopProductCategory> find = new Finder<>(ShopProductCategory.class);

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

    public List<ShopProductCategory> getChildren() {
        return children;
    }

    public void setChildren(List<ShopProductCategory> children) {
        this.children = children;
    }

    public String getPinyinAbbr() {
        return pinyinAbbr;
    }

    public void setPinyinAbbr(String pinyinAbbr) {
        this.pinyinAbbr = pinyinAbbr;
    }

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }
}
