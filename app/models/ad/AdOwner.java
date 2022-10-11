package models.ad;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlAuthoritySerializer;

import javax.persistence.*;


@Entity
@Table(name = "v1_ad_owner")
public class AdOwner extends Model {

    public static final int STATUS_NORMAL = 1;
    public static final int STATUS_OFF = -1;
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "ad_id")
    public long adId;

    @Column(name = "position")
    @JsonDeserialize(using = EscapeHtmlAuthoritySerializer.class)
    public String position;

    @Column(name = "dimension")
    @JsonDeserialize(using = EscapeHtmlAuthoritySerializer.class)
    public String dimension;

    @Column(name = "begin_time")
    public long beginTime;

    @Column(name = "end_time")
    public long endTime;

    @Column(name = "status")
    public int status;

    @Column(name = "sort")
    public int sort;

    @Column(name = "source_url")
    @JsonDeserialize(using = EscapeHtmlAuthoritySerializer.class)
    public String sourceUrl;

    @Column(name = "link_url")
    @JsonDeserialize(using = EscapeHtmlAuthoritySerializer.class)
    public String linkUrl;

    @Column(name = "uid")
    public long uid;

    @Column(name = "update_time")
    public long updateTime;

    @Column(name = "create_time")
    public long createTime;

    public static Finder<Long, AdOwner> find = new Finder<>(AdOwner.class);

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAdId() {
        return adId;
    }

    public void setAdId(long adId) {
        this.adId = adId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
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

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
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

    @Override
    public String toString() {
        return "AdOwner{" +
                "id=" + id +
                ", adId=" + adId +
                ", position='" + position + '\'' +
                ", dimension='" + dimension + '\'' +
                ", beginTime=" + beginTime +
                ", endTime=" + endTime +
                ", status=" + status +
                ", sort=" + sort +
                ", sourceUrl='" + sourceUrl + '\'' +
                ", linkUrl='" + linkUrl + '\'' +
                ", uid=" + uid +
                ", updateTime=" + updateTime +
                ", createTime=" + createTime +
                '}';
    }
}
