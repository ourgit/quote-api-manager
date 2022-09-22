package security;

import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;

/**
 * Created by win7 on 2016/6/9.
 */
public class CorsAction extends Action.Simple {
    public CompletionStage<Result> call(Http.Request req) {

//        Http.Response response = context.response();
//        Logger.logMsg(Logger.DEBUG, "corsAction occurs...");
//        //Handle preflight requests
//        response.setHeader("Access-Control-Allow-Origin", "*");
//        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE");
//        response.setHeader("Access-Control-Max-Age", "3600");
//        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization, X-Auth-Token");
//        response.setHeader("Access-Control-Allow-Credentials", "true");

//        return delegate.call(req).thenApply((result -> result.withHeader("Access-Control-Allow-Origin", "*")));
        return delegate.call(req);
    }

}
