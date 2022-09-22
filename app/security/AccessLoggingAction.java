package security;

import play.Logger;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;

/**
 * access logging action
 */
public class AccessLoggingAction extends Action.Simple {
    private Logger.ALogger accessLogger = Logger.of("access");

    @Override
    public CompletionStage<Result> call(Http.Request req) {
        accessLogger.info("method={} uri={} remote-address={}", req.method(), req.uri(), req.remoteAddress());
        return delegate.call(req);
    }
}
