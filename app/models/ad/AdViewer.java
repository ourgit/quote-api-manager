package models.ad;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlAuthoritySerializer;

import javax.persistence.*;


@Entity
@Table(name = "v1_ad_viewer")
public class AdViewer extends Model {
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "ad_id")
    public long adId;

    @Column(name = "uid")
    public long uid;

    @Column(name = "avatar")
    @JsonDeserialize(using = EscapeHtmlAuthoritySerializer.class)
    public String avatar;

    @Column(name = "create_time")
    public long createdTime;

    public static Finder<String, AdViewer> find = new Finder<>(AdViewer.class);

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAdId() {
        return adId;
    }

    public void setAdId(long adId) {
        this.adId = adId;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String toString() {
        return "AdViewer{" +
                "id=" + id +
                ", adId=" + adId +
                ", uid=" + uid +
                ", avatar='" + avatar + '\'' +
                ", createdTime=" + createdTime +
                '}';
    }
}
