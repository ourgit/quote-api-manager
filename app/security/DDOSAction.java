package security;

import play.Logger;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;

/**
 * 攻击防预
 */
public class DDOSAction extends Action.Simple {
    Logger.ALogger logger = Logger.of(DDOSAction.class);

    public CompletionStage<Result> call(Http.Request request) {
        String ipAddress = request.remoteAddress();
        logger.debug("DDOSAction occurs..." + ipAddress);
        return delegate.call(request);
    }

}
