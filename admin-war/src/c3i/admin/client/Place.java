package c3i.admin.client;

import c3i.featureModel.shared.common.BrandKey;
import c3i.featureModel.shared.common.SeriesKey;
import com.google.common.collect.ImmutableMap;
import smartsoft.util.servlet.shared.QueryString;

import static smartsoft.util.shared.Strings.isEmpty;
import static smartsoft.util.shared.Strings.notEmpty;

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

    public String getViewName() {
        String view = queryString.get("view");
        if (isEmpty(view)) {
            return "exterior";
        }
        return view;
    }

    public BrandKey getBrandKey() {
        String brand = queryString.get("brand");
        if (isEmpty(brand)) {
            return BrandKey.TOYOTA;
        }
        return BrandKey.fromString(brand);
    }

    public String getSeriesName() {
        String s = queryString.get("seriesName");
        if (isEmpty(s)) {
            return null;
        }
        return s.trim();
    }

    public Integer getSeriesYear() {
        String s = queryString.get("seriesYear");
        if (isEmpty(s)) {
            return null;
        }
        return Integer.parseInt(s.trim());
    }

    public SeriesKey getSeriesKey() {
        BrandKey brandKey = getBrandKey();
        String seriesName = getSeriesName();
        Integer seriesYear = getSeriesYear();

        if (brandKey != null && seriesName != null && seriesYear != null) {
            return new SeriesKey(brandKey, seriesYear, seriesName);
        } else {
            return null;
        }
    }

}
