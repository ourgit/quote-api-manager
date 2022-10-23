package models.bid;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlSerializer;

import javax.persistence.*;


@Entity
@Table(name = "v1_bid_detail")
public class BidDetail extends Model {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "bid_id")
    public long bidId;

    @Column(name = "bidder_uid")
    public long bidderUid;

    @Column(name = "bidder_name")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String bidderName;

    @Column(name = "note")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String note;

    @Column(name = "price")
    public long price;

    @Column(name = "update_time")
    public long updateTime;

    @Column(name = "create_time")
    public long createTime;

    public static Finder<Long, BidDetail> find = new Finder<>(BidDetail.class);

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBidId() {
        return bidId;
    }

    public void setBidId(long bidId) {
        this.bidId = bidId;
    }

    public long getBidderUid() {
        return bidderUid;
    }

    public void setBidderUid(long bidderUid) {
        this.bidderUid = bidderUid;
    }

    public String getBidderName() {
        return bidderName;
    }

    public void setBidderName(String bidderName) {
        this.bidderName = bidderName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
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
}
