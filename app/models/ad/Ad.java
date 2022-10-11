package models.ad;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlAuthoritySerializer;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "v1_ad")
public class Ad extends Model {

    public static final int STATUS_NORMAL = 1;
    public static final int STATUS_OFF = -1;
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "position")
    @JsonDeserialize(using = EscapeHtmlAuthoritySerializer.class)
    public String position;

    @Column(name = "dimension")
    @JsonDeserialize(using = EscapeHtmlAuthoritySerializer.class)
    public String dimension;

    @Column(name = "price")
    public long price;

    @Column(name = "days")
    public int days;

    @Column(name = "status")
    public int status;

    @Column(name = "display")
    @JsonDeserialize(using = EscapeHtmlAuthoritySerializer.class)
    public String display;


    @Column(name = "update_time")
    public long updateTime;

    @Column(name = "create_time")
    public long createTime;

    @Transient
    public List<AdOwner> adOwnerList = new ArrayList<>();

    public static Finder<Long, Ad> find = new Finder<>(Ad.class);

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
