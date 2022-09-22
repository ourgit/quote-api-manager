package models.system;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlSerializer;

import javax.persistence.*;


@Entity
@Table(name = "v1_sms_template")
public class SmsTemplate extends Model {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "sort")
    public int sort;

    @Column(name = "enable")
    public boolean enable;

    @Column(name = "content")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String content = "";

    @Column(name = "template_id")

    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String templateId = "";

    public static Finder<Long, SmsTemplate> find = new Finder<>(SmsTemplate.class);

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    @Override
    public String toString() {
        return "SmsTemplate{" +
                "id=" + id +
                ", sort=" + sort +
                ", enable=" + enable +
                ", content='" + content + '\'' +
                ", templateId='" + templateId + '\'' +
                '}';
    }
}
