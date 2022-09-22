package models.stat;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlSerializer;

import javax.persistence.*;

/**
 * 每日注册用户统计
 */
@Entity
@Table(name = "v1_stat_reg")
public class StatisticsReg extends Model {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "total")
    public int total;//注册用户总数

    @Column(name = "date")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String date;//相应日期

    @Column(name = "create_time")
    public long createdTime;


    public static Finder<Long, StatisticsReg> find = new Finder<>(StatisticsReg.class);

    public void setId(long id) {
        this.id = id;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String toString() {
        return "StatisticsReg{" +
                "id=" + id +
                ", total=" + total +
                ", date='" + date + '\'' +
                ", createdTime=" + createdTime +
                '}';
    }
}
