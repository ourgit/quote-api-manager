package filters;

import play.filters.gzip.GzipFilter;
import play.http.HttpFilters;
import play.mvc.EssentialFilter;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class Filters implements HttpFilters {
    private List<EssentialFilter> filters = new ArrayList<>();

    @Inject
    public Filters(ActionLoggingFilter logging, GzipFilter gzipFilter) {
        filters.add(logging);
        filters.add(gzipFilter.asJava());
    }

    @Override
    public List<EssentialFilter> getFilters() {
        return filters;
    }
}
