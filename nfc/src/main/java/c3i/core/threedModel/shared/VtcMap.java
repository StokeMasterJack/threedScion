package c3i.core.threedModel.shared;

import c3i.core.common.shared.SeriesId;
import c3i.core.common.shared.SeriesKey;
import com.google.common.collect.ImmutableMap;

import java.util.logging.Level;
import java.util.logging.Logger;

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
        RootTreeId rootTreeId = vtcMap.get(seriesKey);
        if (rootTreeId == null) {
            String msg = "vtcMap does not contain the key: [" + seriesKey + "]. The valid vtcMap keys are: " + vtcMap.keySet();
            log.log(Level.SEVERE,"A: " + msg);
            throw new IllegalArgumentException("B: " + msg);
        }

        return rootTreeId;
    }

    public SeriesId getSeriesId(SeriesKey seriesKey) {
        RootTreeId rootTreeId = getRootTreeId(seriesKey);
        return new SeriesId(seriesKey, rootTreeId);
    }

    private static Logger log = Logger.getLogger(VtcMap.class.getName());

}
