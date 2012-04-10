package threed.admin.shared;

import threed.repo.shared.Series;
import threed.repo.shared.Settings;
import threed.admin.client.SeriesPickList;

import java.io.Serializable;
import java.util.ArrayList;

public class InitData implements Serializable {

    private static final long serialVersionUID = 2131642088898848026L;

    private /* final */ ArrayList<Series> seriesNameWithYears;

    private /* final */ Settings settings;

    public InitData(ArrayList<Series> seriesNameWithYears, Settings settings) {
        if (seriesNameWithYears == null) {
            throw new IllegalArgumentException("seriesNameWithYears must be non-null");
        }
        if (settings == null) {
            throw new IllegalArgumentException("settings must be non-null");
        }
        this.seriesNameWithYears = seriesNameWithYears;
        this.settings = settings;
    }

    private InitData() {
    }

    public ArrayList<Series> getSeriesNameWithYears() {
        return seriesNameWithYears;
    }

    public SeriesPickList getSeriesPickList() {
        return new SeriesPickList(seriesNameWithYears);
    }

    public Settings getSettings() {
        return settings;
    }


}
