package models.admin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlSerializer;

import javax.persistence.*;

/**
 * 日志
 */
@Entity
@Table(name = "cp_log")
public class Log extends Model {
    @Column(name = "log_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "log_unique")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String logUnique;//防刷唯一系数

    @Column(name = "log_sym_id")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String logSymId;

    @Column(name = "log_mer_id")
    public int memberId;

    @Column(name = "log_param")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String param;

    @Column(name = "log_created")
    public long createdTime;

    public static Finder<Long, Log> find = new Finder<>(Log.class);

    public void setId(long id) {
        this.id = id;
    }

    public void setLogUnique(String logUnique) {
        this.logUnique = logUnique;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getId() {
        return id;
    }

    public String getLogUnique() {
        return logUnique;
    }

    public String getLogSymId() {
        return logSymId;
    }

    public void setLogSymId(String logSymId) {
        this.logSymId = logSymId;
    }

    public int getMemberId() {
        return memberId;
    }

    public String getParam() {
        return param;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    @Override
    public String toString() {
        return "Log{" +
                "id=" + id +
                ", logUnique='" + logUnique + '\'' +
                ", memberId=" + memberId +
                ", param='" + param + '\'' +
                ", createdTime=" + createdTime +
                '}';
    }
}
