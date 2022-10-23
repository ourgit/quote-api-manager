package models.bid;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlSerializer;

import javax.persistence.*;

@Entity
@Table(name = "v1_bid_user")
public class BidUser extends Model {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "user_name")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String userName;

    @Column(name = "bid_id")
    public long bidId;

    @Column(name = "uid")
    public long uid;

    @Column(name = "create_time")
    public long createTime;

    public static Finder<Long, BidUser> find = new Finder<>(BidUser.class);

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getBidId() {
        return bidId;
    }

    public void setBidId(long bidId) {
        this.bidId = bidId;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
