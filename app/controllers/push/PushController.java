package controllers.push;

import akka.actor.ActorSystem;
import akka.stream.Materializer;
import akka.stream.OverflowStrategy;
import controllers.BaseController;
import models.admin.AdminMember;
import play.Logger;
import play.libs.F;
import play.libs.streams.ActorFlow;
import play.mvc.Http;
import play.mvc.WebSocket;
import utils.IdGenerator;
import utils.ValidationUtil;

import javax.inject.Inject;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static constants.BusinessConstant.KEY_AUTH_TOKEN_BY_UID;
import static constants.RedisKeyConstant.ADMIN_KEY_MEMBER_ID_AUTH_TOKEN_PREFIX;


/**
 * 推送
 */
public class PushController extends BaseController {

    Logger.ALogger logger = Logger.of(PushController.class);
    private static final int TICKET_EXPIRE_TIME = 200;//10秒
    public static final String PREFIX_UID = "UID:";
    public static final String KEY_MEMBER_ID = "KEY_MEMBER_ID";

    @Inject
    ActorSystem actorSystem;

    @Inject
    Materializer materializer;

    public WebSocket handleWS() {
        return WebSocket.Json.acceptOrResult(request -> {
            if (!sameOriginCheck(request)) return CompletableFuture.completedFuture(F.Either.Left(forbidden()));
            Optional<AdminMember> memberOptional = getAdminByAuthToken(request);
            AdminMember adminMember = null;
            if (memberOptional.isPresent()) {
                adminMember = memberOptional.get();
            }
            if (null == adminMember) return CompletableFuture.completedFuture(F.Either.Left(forbidden()));
            AdminMember finalAdminMember = adminMember;
            return CompletableFuture.completedFuture(F.Either.Right(
                    ActorFlow.actorRef(
                            out -> MsgActor.props(out, finalAdminMember, IdGenerator.getId()),
                            507800,
                            OverflowStrategy.dropHead(),
                            actorSystem, materializer)));
        });
    }

    public Optional<AdminMember> getAdminByAuthToken(Http.RequestHeader request) {
        String uidToken = "";
        Optional<String> secOption = request.getHeaders().get("Sec-WebSocket-Protocol");
        if (secOption.isPresent()) {
            uidToken = secOption.get();
        }
        if (ValidationUtil.isEmpty(uidToken)) uidToken = getUIDFromRequest(request);
        if (ValidationUtil.isEmpty(uidToken)) {
            uidToken = request.cookie(KEY_AUTH_TOKEN_BY_UID).value();
        }
        if (ValidationUtil.isEmpty(uidToken)) return Optional.empty();
        Optional<String> uidOptional = cache.getOptional(uidToken);
        if (!uidOptional.isPresent()) return Optional.empty();
        String uid = uidOptional.get();
        if (ValidationUtil.isEmpty(uid)) return Optional.empty();
        String key = ADMIN_KEY_MEMBER_ID_AUTH_TOKEN_PREFIX + uid;
        Optional<String> optional = cache.getOptional(key);
        if (!optional.isPresent()) {
            logger.info("key option not present:" + key);
            return Optional.empty();
        }
        String token = optional.get();
        if (ValidationUtil.isEmpty(token)) {
            logger.info("token option not present:" + token);
            return Optional.empty();
        }
        Optional<AdminMember> adminMemberOptional = cache.getOptional(token);
        if (!adminMemberOptional.isPresent()) {
            logger.info("adminMemberOptional option not present:" + token);
            return Optional.empty();
        }
        AdminMember adminMember = adminMemberOptional.get();
        if (null == adminMember) return Optional.empty();
        return Optional.of(adminMember);
    }

    public String getUIDFromRequest(Http.RequestHeader request) {
        Optional<String> authTokenHeaderValues = request.getHeaders().get(KEY_AUTH_TOKEN_BY_UID);
        if (authTokenHeaderValues.isPresent()) {
            String authToken = authTokenHeaderValues.get();
            return authToken;
        }
        return "";
    }

    private boolean sameOriginCheck(Http.RequestHeader request) {
        List<String> origins = request.getHeaders().getAll("Origin");
        if (origins.size() > 1) {
            // more than one origin found
            return false;
        }
        String origin = origins.get(0);
        return originMatches(origin);
    }

    private boolean originMatches(String origin) {
        if (origin == null) return false;
        try {
            URI url = new URI(origin);
            String host = url.getHost();
//            if (!host.equalsIgnoreCase("starnew.cn")
//                    && !host.equalsIgnoreCase("maiyatuan.cn")
//                    && !host.equalsIgnoreCase("localhost")) return false;
//            int port = url.getPort();
//            if (port < 9000 || port > 9010) return false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
