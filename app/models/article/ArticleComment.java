package models.article;

import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 文章评论
 */
@Entity
@Table(name = "v1_article_comment")
public class ArticleComment extends Model {

    public static final int STATUS_SUBMIT = 0;
    public static final int STATUS_ENABLE = 1;
    public static final int STATUS_DISABLE = -1;

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "content")
    private String content;

    @Column(name = "article_id")
    private long articleId;

    @Column(name = "uid")
    private long uid;

    @Column(name = "likes")
    private long likes;

    @Column(name = "create_time")
    private long createdTime;

    @Transient
    public String userName;

    @Transient
    public String avatar;

    @Transient
    public boolean isFollow;

    @Transient
    public boolean hasLike;

    @Transient
    public List<ArticleCommentLike> likeList = new ArrayList<>();

    @Transient
    public List<ArticleCommentReply> replyList = new ArrayList<>();

    public static Finder<Long, ArticleComment> find = new Finder<>(ArticleComment.class);

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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


    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
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

    @Override
    public String toString() {
        return "ArticleComment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", articleId=" + articleId +
                ", uid=" + uid +
                ", likes=" + likes +
                ", createdTime=" + createdTime +
                ", userName='" + userName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", likeList=" + likeList +
                '}';
    }
}
