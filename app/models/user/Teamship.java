package models.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlSerializer;

import javax.persistence.*;


@Entity
@Table(name = "v1_teamship")
public class Teamship extends Model {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "title")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String title;

    @Column(name = "require_order_count")
    public long requireOrderCount;

    @Column(name = "refund_point")
    public double refundPoint;

    @Column(name = "sort")
    public int sort;

    @Column(name = "salary")
    public long salary;

    @Column(name = "level")
    public int level;

    @Column(name = "need_show")
    public boolean needShow;

    @Column(name = "detail")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String detail;

    public static Finder<Long, Teamship> find = new Finder<>(Teamship.class);

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getRequireOrderCount() {
        return requireOrderCount;
    }

    public void setRequireOrderCount(long requireOrderCount) {
        this.requireOrderCount = requireOrderCount;
    }

    public double getRefundPoint() {
        return refundPoint;
    }

    public void setRefundPoint(double refundPoint) {
        this.refundPoint = refundPoint;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public long getSalary() {
        return salary;
    }

    public void setSalary(long salary) {
        this.salary = salary;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isNeedShow() {
        return needShow;
    }

    public void setNeedShow(boolean needShow) {
        this.needShow = needShow;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "Teamship{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", requireOrderCount=" + requireOrderCount +
                ", refundPoint=" + refundPoint +
                ", sort=" + sort +
                ", salary=" + salary +
                ", level=" + level +
                ", needShow=" + needShow +
                ", detail='" + detail + '\'' +
                '}';
    }
}
