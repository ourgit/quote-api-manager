package models.post;

import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;

/**
 * 商品分类
 */
@Entity
@Table(name = "v1_post")
public class PostLike extends Model {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "uid")
    public long uid;
    @Column(name = "post_id")
    public long postId;

    @Column(name = "like")
    public boolean like;

    @Column(name = "update_time")
    public long updateTime;

    public static Finder<Long, PostLike> find = new Finder<>(PostLike.class);

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }
}
