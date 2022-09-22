package models.log;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ebean.Finder;
import io.ebean.Model;
import myannotation.EscapeHtmlSerializer;

import javax.persistence.*;

/**
 * 登录日志表,以用户id来分表
 */
@Entity
@Table(name = "v1_sms_log")
public class SmsLog extends Model {
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "msg_id")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String msgId;

    @Column(name = "phone_number")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String phoneNumber;

    @Column(name = "content")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String content;

    @Column(name = "extno")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String extno;

    @Column(name = "req_status")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String reqStatus;

    @Column(name = "resp_status")
    @JsonDeserialize(using = EscapeHtmlSerializer.class)
    public String respStatus;

    @Column(name = "req_time")
    public long reqTime;

    @Column(name = "resp_time")
    public long respTime;

    @Column(name = "query_resp_time")
    public long queryRespTime;

    public static Finder<Long, SmsLog> find = new Finder<>(SmsLog.class);

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExtno() {
        return extno;
    }

    public void setExtno(String extno) {
        this.extno = extno;
    }

    public String getReqStatus() {
        return reqStatus;
    }

    public void setReqStatus(String reqStatus) {
        this.reqStatus = reqStatus;
    }

    public String getRespStatus() {
        return respStatus;
    }

    public void setRespStatus(String respStatus) {
        this.respStatus = respStatus;
    }

    public long getReqTime() {
        return reqTime;
    }

    public void setReqTime(long reqTime) {
        this.reqTime = reqTime;
    }

    public long getRespTime() {
        return respTime;
    }

    public void setRespTime(long respTime) {
        this.respTime = respTime;
    }

    public long getQueryRespTime() {
        return queryRespTime;
    }

    public void setQueryRespTime(long queryRespTime) {
        this.queryRespTime = queryRespTime;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    @Override
    public String toString() {
        return "SmsLog{" +
                "id=" + id +
                ", msgId='" + msgId + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", content='" + content + '\'' +
                ", extno='" + extno + '\'' +
                ", reqStatus='" + reqStatus + '\'' +
                ", respStatus='" + respStatus + '\'' +
                ", reqTime=" + reqTime +
                ", respTime=" + respTime +
                ", queryRespTime=" + queryRespTime +
                '}';
    }
}
