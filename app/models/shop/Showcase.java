package models.shop;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlSerializer;

import javax.persistence.*;

@Entity
@Table(name = "v1_show_case")
public class Showcase extends Model {

    public static final int STATUS_NOT_AUDIT = 1;
    public static final int STATUS_AUDIT = 2;
    public static final int STATUS_NOT_DENY = -1;

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "title")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String title;

    @Column(name = "cover")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String cover;

    @Column(name = "tags")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String tags;

    @Column(name = "images")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String images;//联系电话

    @Column(name = "image_count")
    public long imageCount;//联系电话

    @Column(name = "shop_name")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String shopName;//联系人

    @Column(name = "shop_logo")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String shopLogo;

    @Column(name = "place_top")
    public boolean placeTop;

    @Column(name = "shop_id")
    public long shopId;//创建者uid

    @Column(name = "status")
    public int status;

    @Column(name = "sort")
    public int sort;

    @Column(name = "create_time")
    public long createTime;

    public static Finder<Long, Showcase> find = new Finder<>(Showcase.class);

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public long getImageCount() {
        return imageCount;
    }

    public void setImageCount(long imageCount) {
        this.imageCount = imageCount;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getShopLogo() {
        return shopLogo;
    }

    public void setShopLogo(String shopLogo) {
        this.shopLogo = shopLogo;
    }

    public boolean isPlaceTop() {
        return placeTop;
    }

    public void setPlaceTop(boolean placeTop) {
        this.placeTop = placeTop;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
