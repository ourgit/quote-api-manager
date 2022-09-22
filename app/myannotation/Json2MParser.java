package myannotation;

import play.http.HttpErrorHandler;
import play.mvc.BodyParser;

import javax.inject.Inject;

/**
 * Created by Administrator on 2017/5/12.
 */
public class Json2MParser extends BodyParser.Json {
    @Inject
    public Json2MParser(HttpErrorHandler errorHandler) {
        super(2 * 1024 * 1024, errorHandler);
    }
}
