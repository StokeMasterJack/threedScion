package c3i.admin.client;

import c3i.core.common.shared.BrandKey;
import c3i.repo.shared.Series;

import java.util.ArrayList;

public class SeriesPickList {

    private final BrandKey brandKey;
    private final ArrayList<Series> seriesList;

    public SeriesPickList(BrandKey brandKey, ArrayList<Series> seriesList) {
        this.brandKey = brandKey;
        this.seriesList = seriesList;
    }

    public ArrayList<Series> getSeriesList() {
        return seriesList;
    }

    public BrandKey getBrandKey() {
        return brandKey;
    }
}
