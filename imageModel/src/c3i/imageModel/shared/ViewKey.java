package c3i.imageModel.shared;


import c3i.featureModel.shared.common.SeriesKey;
import smartsoft.util.shared.Path;

import static com.google.common.base.Preconditions.checkNotNull;

public class ViewKey {

    private final SeriesKey seriesKey;
    private final int viewIndex;

    public ViewKey(SeriesKey seriesKey, int viewIndex) {
        checkNotNull(seriesKey);
        this.seriesKey = seriesKey;
        this.viewIndex = viewIndex;
    }

    public SeriesKey getSeriesKey() {
        return seriesKey;
    }

    public int getViewIndex() {
        return viewIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ViewKey viewKey = (ViewKey) o;

        if (viewIndex != viewKey.viewIndex) return false;
        if (!seriesKey.equals(viewKey.seriesKey)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = seriesKey.hashCode();
        result = 31 * result + viewIndex;
        return result;
    }

    @Override
    public String toString() {
        return seriesKey + "  View[" + viewIndex + "]";
    }

    public Path getPath() {
//        checkState(seriesKey != null);
        Path localPath = seriesKey.getLocalPath();
        return new Path("v" + viewIndex).prepend(localPath);
    }
}
