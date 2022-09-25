package models.system;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlSerializer;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 配置
 */
@Entity
@Table(name = "v1_system_config")
public class ParamConfig extends Model implements Serializable {
    public static final int SOURCE_FRONTEND = 1;
    public static final int SOURCE_BACKEND = 2;

    public static final int CONTENT_TYPE_TEXT = 1;
    public static final int CONTENT_TYPE_IMAGE = 2;
    public static final int CONTENT_TYPE_COLOR = 3;
    public static final int CONTENT_TYPE_SWITCH = 4;
    public static final int CONTENT_TYPE_JSON = 5;


    private static final long serialVersionUID = 1885841224604019893L;
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "config_key")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String key;

    @Column(name = "config_value")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String value;

    @Column(name = "note")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String note;

    @Column(name = "enable")
    public boolean enable;

    @Column(name = "source")
    public int source;

    @Column(name = "content_type")
    public int contentType;

    @Column(name = "is_encrypt")
    public boolean isEncrypt;

    @Column(name = "update_time")
    public long updateTime;

    public static Finder<Integer, ParamConfig> find = new Finder<>(ParamConfig.class);

    public void setId(int id) {
        this.id = id;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getNote() {
        return note;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public boolean isEncrypt() {
        return isEncrypt;
    }

    public void setEncrypt(boolean encrypt) {
        isEncrypt = encrypt;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }
}
