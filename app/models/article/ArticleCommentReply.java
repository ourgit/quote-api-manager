package models.article;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlSerializer;

import javax.persistence.*;

/**
 * 文章回复
 */
@Entity
@Table(name = "v1_article_comment_reply")
public class ArticleCommentReply extends Model {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "content")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    private String content;

    @Column(name = "article_comment_id")
    private long commentId;

    @Column(name = "at_uid")
    private long atUid;

    @Column(name = "uid")
    private long uid;

    @Column(name = "create_time")
    private long createdTime;

    @Transient
    public String userName;

    @Transient
    public String avatar;

    @Transient
    public String atUserName;

    @Transient
    public String atUserAvatar;


    public static Finder<Long, ArticleCommentReply> find = new Finder<>(ArticleCommentReply.class);

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

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public long getAtUid() {
        return atUid;
    }

    public void setAtUid(long atUid) {
        this.atUid = atUid;
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

    public String getAtUserName() {
        return atUserName;
    }

    public void setAtUserName(String atUserName) {
        this.atUserName = atUserName;
    }

    public String getAtUserAvatar() {
        return atUserAvatar;
    }

    public void setAtUserAvatar(String atUserAvatar) {
        this.atUserAvatar = atUserAvatar;
    }
}
