package models.msg;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlSerializer;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "v1_msg")
public class Msg extends Model implements Serializable {

    public static final int STATUS_NOT_READ = 1;
    public static final int STATUS_READ = 2;

    public static final int MSG_TYPE_BALANCE = 1;
    public static final int MSG_TYPE_TRANSACTIONS = 2;
    public static final int MSG_TYPE_SYSTEM = 3;

    private static final long serialVersionUID = -1885841224604019263L;

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "uid")
    public long uid;//用户id

    @Column(name = "item_id")
    public int itemId;//业务ID

    @Column(name = "change_amount")
    public long changeAmount;

    @Column(name = "title")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String title;

    @Column(name = "content")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String content;

    @Column(name = "link_url")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String linkUrl;

    @Column(name = "msg_type")
    public int msgType;

    @Column(name = "category_id")
    public int categoryId;

    @Column(name = "article_id")
    public long articleId;

    @Column(name = "status")
    public int status;

    @Column(name = "create_time")
    public long createTime;//创建时间

    public static Finder<Long, Msg> find = new Finder<>(Msg.class);

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

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public long getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(long changeAmount) {
        this.changeAmount = changeAmount;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public long getArticleId() {
        return articleId;
    }

    public void setArticleId(long articleId) {
        this.articleId = articleId;
    }
}
