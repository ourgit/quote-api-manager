package controllers.push;

import actor.ManageProtocol;
import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.admin.AdminMember;
import play.libs.Json;
import utils.ValidationUtil;

import java.util.*;

import static constants.BusinessConstant.*;

/**
 * 推送ACTOR
 */
public class MsgActor extends AbstractLoggingActor {
    private static final Map<Long, ActorRef> map = new WeakHashMap<>();
    private static final Set<ActorRef> set = new HashSet<>();

    public static Props props(ActorRef out, AdminMember adminMember, long id) {
        return Props.create(MsgActor.class, out, adminMember, id);
    }

    public static final String WS_TYPE_HELLO = "hello";
    public static final String WS_TYPE_PING = "ping";
    public static final String WS_TYPE_REQ = "req";
    public static final String WS_TYPE_SUB = "sub";
    public static final String WS_TYPE_UNSUB = "unsub";

    private final AdminMember adminMember;
    private final long id;
    ActorRef out;

    public MsgActor(ActorRef out, AdminMember adminMember, long id) {
        this.adminMember = adminMember;
        this.id = id;
        if (null != out) {
            this.out = out;
            if (null != adminMember && adminMember.id > 0) map.put(adminMember.id, out);
            set.add(out);
            ObjectNode node = Json.newObject();
            node.put("type", WS_TYPE_HELLO);
            node.put("ts", System.currentTimeMillis());
            out.tell(node, self());
        }
    }

    @Override
    public void postStop() throws Exception {
        if (null != adminMember) map.remove(adminMember.id);
        if (null != out) set.remove(out);
        super.postStop();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                //推送事件
                .match(ManageProtocol.CHANNEL_MSGS.class, msg -> {
                    log().info("MsgActor:" + msg.content);
                    if (!ValidationUtil.isEmpty(msg.content)) {
                        ObjectNode source = (ObjectNode) Json.parse(msg.content);
                        source.put("currentTime", System.currentTimeMillis() / 1000);
                        String type = source.findPath("type").asText();
                        List<AdminMember> list = AdminMember.find.query().where().eq("status", AdminMember.STATUS_NORMAL).findList();
                        list.forEach((each) -> {
                            if (null != each) {
                                ActorRef outActor = map.get(each.id);
                                if (null != outActor) outActor.tell(source, ActorRef.noSender());
                            }
                        });
                    }
                })
                .match(ObjectNode.class, param -> {
                    String type = param.findPath("type").asText();
                    if (!ValidationUtil.isEmpty(type) && type.equalsIgnoreCase(PING)) {
                        ObjectNode node = Json.newObject();
                        node.put("type", PONG);
                        node.put("ts", System.currentTimeMillis());
                        if (null != getSender()) getSender().tell(node, ActorRef.noSender());
                    }
                })
                .build();
    }


    @Override
    public void unhandled(Object message) {
        super.unhandled(message);
    }

}
