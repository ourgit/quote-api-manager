package models.shop;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlSerializer;
import myannotation.EscapeHtmlSerializerForKeepSomeHtml;

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
    public long id;

    @Column(name = "title")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String title;


    @Column(name = "tags")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String tags;

    @Column(name = "images")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String images;

    @Column(name = "cover")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String cover;

    @Column(name = "image_count")
    public long imageCount;//联系电话

    @Column(name = "category_id")
    public long categoryId;

    @Column(name = "shop_name")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String shopName;//联系人

    @Column(name = "shop_logo")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String shopLogo;

    @Column(name = "content")
    @JsonDeserialize(using = EscapeHtmlSerializerForKeepSomeHtml.class)
    public String content;

    @Column(name = "place_top")
    public boolean placeTop;

    @Column(name = "shop_id")
    public long shopId;

    @Column(name = "status")
    public int status;

    @Column(name = "sort")
    public int sort;

    @Column(name = "create_time")
    public long createTime;

    public static Finder<Long, Showcase> find = new Finder<>(Showcase.class);

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public long getImageCount() {
        return imageCount;
    }

    public void setImageCount(long imageCount) {
        this.imageCount = imageCount;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopLogo() {
        return shopLogo;
    }

    public void setShopLogo(String shopLogo) {
        this.shopLogo = shopLogo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isPlaceTop() {
        return placeTop;
    }

    public void setPlaceTop(boolean placeTop) {
        this.placeTop = placeTop;
    }

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
