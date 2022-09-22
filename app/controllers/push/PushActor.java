package controllers.push;

import actor.ManageProtocol;
import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;


/**
 * 推送ACTOR
 */
public class PushActor extends AbstractLoggingActor {

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                //推送事件
                .match(ManageProtocol.CHANNEL_MSGS.class, msg -> {
                    log().info("ManageProtocol:" + msg.content);
                    ActorRef msgActor = this.getContext().getSystem().actorOf(MsgActor.props(null, null, 0));
                    msgActor.tell(msg, getSelf());
                })
                .build();
    }


    @Override
    public void unhandled(Object message) {
        super.unhandled(message);
    }

}
