package models.shop;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlSerializer;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 轮播表
 */
@Entity
@Table(name = "v1_new_shop_plan")
public class NewShopPlan extends Model implements Serializable {
    private static final long serialVersionUID = 4087383400213282958L;
    public static final int TYPE_PC = 1;
    public static final int TYPE_MOBILE = 2;

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    private String name;

    @Column(name = "img_url")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    private String imgUrl;

    @Column(name = "link_url")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    private String linkUrl;

    @Column(name = "cover_url")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    private String coverUrl;

    @Column(name = "client_type")
    private int clientType;

    @Column(name = "biz_type")
    private int bizType;

    @Column(name = "sort")
    private int sort;

    @Column(name = "need_show")
    private boolean needShow;

    @Column(name = "title1")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    private String title1;

    @Column(name = "title2")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    private String title2;

    @Column(name = "note")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    private String note;

    @Column(name = "update_time")
    private long updateTime;

    @Column(name = "create_time")
    private long createTime;


    public static Finder<Integer, NewShopPlan> find = new Finder<>(NewShopPlan.class);

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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public int getBizType() {
        return bizType;
    }

    public void setBizType(int bizType) {
        this.bizType = bizType;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public boolean isNeedShow() {
        return needShow;
    }

    public void setNeedShow(boolean needShow) {
        this.needShow = needShow;
    }

    public String getTitle1() {
        return title1;
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
}
