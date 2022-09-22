package models.article;

import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;

/**
 * 文章评论点赞
 */
@Entity
@Table(name = "v1_article_comment_like")
public class ArticleCommentLike extends Model {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "comment_id")
    private long commentId;

    @Column(name = "has_like")
    private boolean hasLike;

    @Column(name = "uid")
    private long uid;

    @Column(name = "create_time")
    private long createdTime;

    @Transient
    public String userName;

    @Transient
    public String avatar;

    public static Finder<Long, ArticleCommentLike> find = new Finder<>(ArticleCommentLike.class);

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isHasLike() {
        return hasLike;
    }

    public void setHasLike(boolean hasLike) {
        this.hasLike = hasLike;
    }

    @Override
    public String toString() {
        return "BarCommentLike{" +
                "id=" + id +
                ", commentId=" + commentId +
                ", like=" + hasLike +
                ", uid=" + uid +
                ", createdTime=" + createdTime +
                ", userName='" + userName + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
