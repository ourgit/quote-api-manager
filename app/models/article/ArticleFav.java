package models.article;

import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;

/**
 * 文章收藏
 */
@Entity
@Table(name = "v1_article_fav")
public class ArticleFav extends Model {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "article_id")
    private long articleId;

    @Column(name = "uid")
    private long uid;

    @Column(name = "enable")
    private boolean enable;

    @Column(name = "create_time")
    private long createdTime;

    public static Finder<Long, ArticleFav> find = new Finder<>(ArticleFav.class);

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getArticleId() {
        return articleId;
    }

    public void setArticleId(long articleId) {
        this.articleId = articleId;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String toString() {
        return "ArticleFav{" +
                "id=" + id +
                ", articleId=" + articleId +
                ", uid=" + uid +
                ", enable=" + enable +
                ", createdTime=" + createdTime +
                '}';
    }
}
