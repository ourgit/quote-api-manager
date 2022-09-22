package models.article;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlSerializer;
import myannotation.EscapeHtmlSerializerForKeepSomeHtml;

import javax.persistence.*;

/**
 * 文章表
 */
@Entity
@Table(name = "v1_article")
public class Article extends Model {

    //1正常2禁用
    public static final int ARTICLE_STATUS_NORMAL = 1;
    public static final int ARTICLE_STATUS_DISABLE = 2;
    //是否置顶 1否 2是
    public static final int ARTICLE_TOP_NO = 1;
    public static final int ARTICLE_TOP_YES = 2;

    //是否推荐 1否2 是
    public static final int ARTICLE_RECOMMEND_NO = 1;
    public static final int ARTICLE_RECOMMEND_YES = 2;


    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    private String title;//文章标题

    @Column(name = "author")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    private String author;//文章作者

    @Column(name = "source")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    private String source;//文章来源

    @Column(name = "cate_id")
    private int categoryId;//文章分类

    @Column(name = "publish_time")
    private long publishTime;//文章发布时间

    @Column(name = "status")
    private int status;//1正常2禁用

    @Column(name = "is_top")
    private boolean isTop;//是否置顶 1否 2是

    @Column(name = "is_recommend")
    private boolean isRecommend;//是否推荐 1否2 是

    @Column(name = "sort")
    private int sort;//排序

    @Column(name = "content")
    @JsonDeserialize(using = EscapeHtmlSerializerForKeepSomeHtml.class)
    private String content;

    @Column(name = "digest")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    private String digest;

    @Column(name = "head_pic")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    private String headPic;

    @Column(name = "tags")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    private String tags;

    @Column(name = "product_id_list")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    private String productIdList;

    @Column(name = "views")
    private long views;

    @Column(name = "favs")
    private long favs;

    @Column(name = "comments")
    private long comments;

    @Column(name = "shares")
    private long shares;

    @Column(name = "update_time")
    private long updateTime;

    @Column(name = "create_time")
    private long createdTime;

    @Transient
    private String categoryName;

    @Transient
    public boolean isFav;

    @Transient
    private String publishDay;//文章发布日期，只显示几号

    public static Finder<Long, Article> find = new Finder<>(Article.class);

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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(long publishTime) {
        this.publishTime = publishTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    public boolean isRecommend() {
        return isRecommend;
    }

    public void setRecommend(boolean recommend) {
        isRecommend = recommend;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getPublishDay() {
        return publishDay;
    }

    public void setPublishDay(String publishDay) {
        this.publishDay = publishDay;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }

    public long getFavs() {
        return favs;
    }

    public void setFavs(long favs) {
        this.favs = favs;
    }

    public long getShares() {
        return shares;
    }

    public void setShares(long shares) {
        this.shares = shares;
    }

    public long getComments() {
        return comments;
    }

    public void setComments(long comments) {
        this.comments = comments;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getProductIdList() {
        return productIdList;
    }

    public void setProductIdList(String productIdList) {
        this.productIdList = productIdList;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", source='" + source + '\'' +
                ", categoryId=" + categoryId +
                ", publishTime=" + publishTime +
                ", status=" + status +
                ", isTop=" + isTop +
                ", isRecommend=" + isRecommend +
                ", sort=" + sort +
                ", content='" + content + '\'' +
                ", digest='" + digest + '\'' +
                ", headPic='" + headPic + '\'' +
                ", views=" + views +
                ", favs=" + favs +
                ", comments=" + comments +
                ", shares=" + shares +
                ", updateTime=" + updateTime +
                ", createdTime=" + createdTime +
                ", categoryName='" + categoryName + '\'' +
                ", publishDay='" + publishDay + '\'' +
                '}';
    }
}
