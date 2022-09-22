package models.ad;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlAuthoritySerializer;

import javax.persistence.*;


@Entity
@Table(name = "v1_ad")
public class Ad extends Model {

    public static final int STATUS_NORMAL = 1;
    public static final int STATUS_OFF = -1;
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "avatar")
    @JsonDeserialize(using = EscapeHtmlAuthoritySerializer.class)
    public String avatar;

    @Column(name = "title")
    @JsonDeserialize(using = EscapeHtmlAuthoritySerializer.class)
    public String title;

    @Column(name = "digest")
    @JsonDeserialize(using = EscapeHtmlAuthoritySerializer.class)
    public String digest;

    @Column(name = "content")
    @JsonDeserialize(using = EscapeHtmlAuthoritySerializer.class)
    public String content;

    @Column(name = "img1")
    @JsonDeserialize(using = EscapeHtmlAuthoritySerializer.class)
    public String img1;

    @Column(name = "img2")
    @JsonDeserialize(using = EscapeHtmlAuthoritySerializer.class)
    public String img2;

    @Column(name = "img3")
    @JsonDeserialize(using = EscapeHtmlAuthoritySerializer.class)
    public String img3;

    @Column(name = "img4")
    @JsonDeserialize(using = EscapeHtmlAuthoritySerializer.class)
    public String img4;

    @Column(name = "img5")
    @JsonDeserialize(using = EscapeHtmlAuthoritySerializer.class)
    public String img5;

    @Column(name = "img6")
    @JsonDeserialize(using = EscapeHtmlAuthoritySerializer.class)
    public String img6;

    @Column(name = "img7")
    @JsonDeserialize(using = EscapeHtmlAuthoritySerializer.class)
    public String img7;

    @Column(name = "img8")
    @JsonDeserialize(using = EscapeHtmlAuthoritySerializer.class)
    public String img8;

    @Column(name = "img9")
    @JsonDeserialize(using = EscapeHtmlAuthoritySerializer.class)
    public String img9;

    @Column(name = "link_url")
    @JsonDeserialize(using = EscapeHtmlAuthoritySerializer.class)
    public String linkUrl;

    @Column(name = "budget")
    public long budget;

    @Column(name = "views")
    public long views;

    @Column(name = "status")
    public int status;

    @Column(name = "sort")
    public long sort;

    @Column(name = "begin_time")
    public long beginTime;

    @Column(name = "end_time")
    public long endTime;

    @Column(name = "article_id")
    public long articleId;

    @Column(name = "update_time")
    public long updateTime;

    @Column(name = "create_time")
    public long createTime;

    public static Finder<Long, Ad> find = new Finder<>(Ad.class);


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getImg3() {
        return img3;
    }

    public void setImg3(String img3) {
        this.img3 = img3;
    }

    public String getImg4() {
        return img4;
    }

    public void setImg4(String img4) {
        this.img4 = img4;
    }

    public String getImg5() {
        return img5;
    }

    public void setImg5(String img5) {
        this.img5 = img5;
    }

    public String getImg6() {
        return img6;
    }

    public void setImg6(String img6) {
        this.img6 = img6;
    }

    public String getImg7() {
        return img7;
    }

    public void setImg7(String img7) {
        this.img7 = img7;
    }

    public String getImg8() {
        return img8;
    }

    public void setImg8(String img8) {
        this.img8 = img8;
    }

    public String getImg9() {
        return img9;
    }

    public void setImg9(String img9) {
        this.img9 = img9;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getSort() {
        return sort;
    }

    public void setSort(long sort) {
        this.sort = sort;
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

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public long getBudget() {
        return budget;
    }

    public void setBudget(long budget) {
        this.budget = budget;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public long getArticleId() {
        return articleId;
    }

    public void setArticleId(long articleId) {
        this.articleId = articleId;
    }

    @Override
    public String toString() {
        return "Ad{" +
                "id=" + id +
                ", avatar='" + avatar + '\'' +
                ", title='" + title + '\'' +
                ", digest='" + digest + '\'' +
                ", content='" + content + '\'' +
                ", img1='" + img1 + '\'' +
                ", img2='" + img2 + '\'' +
                ", img3='" + img3 + '\'' +
                ", img4='" + img4 + '\'' +
                ", img5='" + img5 + '\'' +
                ", img6='" + img6 + '\'' +
                ", img7='" + img7 + '\'' +
                ", img8='" + img8 + '\'' +
                ", img9='" + img9 + '\'' +
                ", linkUrl='" + linkUrl + '\'' +
                ", budget=" + budget +
                ", views=" + views +
                ", status=" + status +
                ", sort=" + sort +
                ", beginTime=" + beginTime +
                ", endTime=" + endTime +
                ", articleId=" + articleId +
                ", updateTime=" + updateTime +
                ", createTime=" + createTime +
                '}';
    }
}
