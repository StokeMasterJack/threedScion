package threed.admin.client;

import threed.repo.shared.Series;

import java.util.ArrayList;

public class SeriesPickList {

    private final ArrayList<Series> seriesList;

    public SeriesPickList(ArrayList<Series> seriesList) {
        this.seriesList = seriesList;
    }

    public ArrayList<Series> getSeriesList() {
        return seriesList;
    }

}
