package filters;

import akka.stream.Materializer;
import models.log.OperationLog;
import play.mvc.Filter;
import play.mvc.Http;
import play.mvc.Result;
import utils.ValidationUtil;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class ActionLoggingFilter extends Filter {

    @Inject
    public ActionLoggingFilter(Materializer mat) {
        super(mat);
    }

    @Override
    public CompletionStage<Result> apply(
            Function<Http.RequestHeader, CompletionStage<Result>> nextFilter,
            Http.RequestHeader requestHeader) {
        return nextFilter
                .apply(requestHeader)
                .thenApply(
                        result -> {
                            String adminId = requestHeader.getHeaders().get("adminId").orElse("0");
                            String adminName = requestHeader.getHeaders().get("adminName").orElse("");
                            String ip = requestHeader.getHeaders().get("ip").orElse("");
                            String place = requestHeader.getHeaders().get("place").orElse("");
                            String note = requestHeader.getHeaders().get("note").orElse("");
                            if (!ValidationUtil.isEmpty(note)) {
                                OperationLog log = new OperationLog();
                                log.setCreateTime(System.currentTimeMillis() / 1000);
                                log.setIp(ip);
                                log.setAdminId(Long.parseLong(adminId));
                                log.setAdminName(adminName);
                                log.setPlace(place);
                                log.setNote(note);
                                log.save();
                            }
                            return result;
                        });
    }
}
