package threed.admin.client;

import com.google.common.collect.ImmutableMap;
import threed.core.threedModel.shared.SeriesKey;
import smartsoft.util.servlet.shared.QueryString;

import static smartsoft.util.lang.shared.Strings.isEmpty;
import static smartsoft.util.lang.shared.Strings.notEmpty;

public class Place {

    private final ImmutableMap<String, String> queryString;

    public Place(ImmutableMap<String, String> queryString) {
        this.queryString = queryString;
    }

    public static Place createFromToken(String token) {
        if (notEmpty(token)) {
            ImmutableMap<String, String> queryString = QueryString.parse(token);
            return new Place(queryString);
        } else {
            ImmutableMap<String, String> of = ImmutableMap.of();
            return new Place(of);
        }
    }

    public ImmutableMap<String, String> getQueryString() {
        return queryString;
    }

    public String get(String name) {
        return queryString.get(name);
    }

    public SeriesKey getSeriesKey() {
        String seriesKey = queryString.get("seriesKey");
        if (isEmpty(seriesKey)) {
            return null;
        }
        return SeriesKey.parse(seriesKey);
    }

}
