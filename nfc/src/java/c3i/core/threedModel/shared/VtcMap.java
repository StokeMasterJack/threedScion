package c3i.core.threedModel.shared;

import c3i.featureModel.shared.common.RootTreeId;
import c3i.featureModel.shared.common.SeriesId;
import c3i.featureModel.shared.common.SeriesKey;
import com.google.common.collect.ImmutableMap;

public class VtcMap {

    private ImmutableMap<SeriesKey, RootTreeId> vtcMap;

    public VtcMap(ImmutableMap<SeriesKey, RootTreeId> vtcMap) {
        this.vtcMap = vtcMap;
    }

    public ImmutableMap<SeriesKey, RootTreeId> toMap() {
        return vtcMap;
    }

    public static ImmutableMap<SeriesKey, RootTreeId> parse(String serial) {
        String[] lines = serial.split("\n");
        ImmutableMap.Builder<SeriesKey, RootTreeId> builder = ImmutableMap.builder();
        for (String line : lines) {
            if (line != null && line.trim().length() != 0) {
                String[] a = line.split(":");

                SeriesKey seriesKey = SeriesKey.parse(a[0]);
                RootTreeId treeId = new RootTreeId(a[1]);
                builder.put(seriesKey, treeId);
            }
        }
        return builder.build();
    }

    public RootTreeId getRootTreeId(SeriesKey seriesKey) {
        return vtcMap.get(seriesKey);
    }

    public SeriesId getSeriesId(SeriesKey seriesKey) {
        RootTreeId rootTreeId = getRootTreeId(seriesKey);
        return new SeriesId(seriesKey, rootTreeId);
    }

}
