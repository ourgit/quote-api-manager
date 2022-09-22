package models.system;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlSerializer;

import javax.persistence.*;

/**
 * 用户字典
 */
@Entity
@Table(name = "v1_user_dict")
public class UserDict extends Model {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "uid")
    public long uid;

    @Column(name = "count")
    public long count;

    @Column(name = "dict_name")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String dictName = "";

    @Column(name = "cate_name")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String cateName = "";

    @Column(name = "pinyin_abbr")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String pinyinAbbr = "";

    public static Finder<Long, UserDict> find = new Finder<>(UserDict.class);

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

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    public String getPinyinAbbr() {
        return pinyinAbbr;
    }

    public void setPinyinAbbr(String pinyinAbbr) {
        this.pinyinAbbr = pinyinAbbr;
    }

    public String getCateName() {
        return cateName;
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    @Override
    public String toString() {
        return "UserDict{" +
                "id=" + id +
                ", uid=" + uid +
                ", count=" + count +
                ", dictName='" + dictName + '\'' +
                ", cateName='" + cateName + '\'' +
                ", pinyinAbbr='" + pinyinAbbr + '\'' +
                '}';
    }
}
