package models.admin;

import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;

/**
 * 用户评论数
 */
@Entity
@Table(name = "v1_member_comment_number")
public class MemberCommentNumber extends Model {

    public static final int TYPE_TIMELINE_COMMENT = 1;
    public static final int TYPE_BAR_COMMENT = 2;
    public static final int TYPE_ACTIVITY_COMMENT = 3;
    public static final int TYPE_ARTICLE_COMMENT = 4;
    public static final int TYPE_WINE_COMMENT = 5;

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "uid")
    public long uid;//

    @Column(name = "comment_type")
    public int commentType;//用户等级

    @Column(name="number")
    public int number;

    public static Finder<Long, MemberCommentNumber> find = new Finder<>(MemberCommentNumber.class);

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

    public int getCommentType() {
        return commentType;
    }

    public void setCommentType(int commentType) {
        this.commentType = commentType;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "MemberCommentNumber{" +
                "id=" + id +
                ", uid=" + uid +
                ", commentType=" + commentType +
                ", number=" + number +
                '}';
    }
}
