package models.system;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlSerializer;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 字典
 */
@Entity
@Table(name = "v1_dict")
public class Dict extends Model {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "parent_id")
    public long parentId;

    @Column(name = "sort")
    public int sort;

    @Column(name = "path")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String path = "";

    @Column(name = "name")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String name = "";

    @Column(name = "attr")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String attr = "";

    @Column(name = "attr_value")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String attrValue = "";

    @Column(name = "pinyin_abbr")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String pinyinAbbr = "";

    @Column(name = "lat")
    public double lat;

    @Column(name = "lng")
    public double lng;

    @Column(name = "create_time")
    public long createTime;

    @Transient
    public List<Dict> children = new ArrayList<>();

    public static Finder<Long, Dict> find = new Finder<>(Dict.class);

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public List<Dict> getChildren() {
        return children;
    }

    public void setChildren(List<Dict> children) {
        this.children = children;
    }

    public String getPinyinAbbr() {
        return pinyinAbbr;
    }

    public void setPinyinAbbr(String pinyinAbbr) {
        this.pinyinAbbr = pinyinAbbr;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public String getAttrValue() {
        return attrValue;
    }

    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue;
    }
}
