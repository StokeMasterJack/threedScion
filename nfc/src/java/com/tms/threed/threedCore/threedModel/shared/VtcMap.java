package com.tms.threed.threedCore.threedModel.shared;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class VtcMap {

    private final ImmutableMap<SeriesKey, RootTreeId> map;

    public VtcMap(ImmutableMap<SeriesKey, RootTreeId> map) {
        this.map = map;
    }

    public VtcMap() {
        ImmutableMap.Builder<SeriesKey, RootTreeId> builder = ImmutableMap.builder();
        builder.put(SeriesKey.AVALON_2011, new RootTreeId("da9f24132588c5c4f488af60b77ab1c2271669cc"));
        map = builder.build();
    }

    public RootTreeId getVtcVersion(SeriesKey seriesKey) {
        return map.get(seriesKey);
    }

    public Map<SeriesKey, RootTreeId> toMap() {
        return map;
    }

    public String serialize() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<SeriesKey, RootTreeId> entry : map.entrySet()) {
            SeriesKey key = entry.getKey();
            RootTreeId value = entry.getValue();
            sb.append(key.toString());
            sb.append(":");
            sb.append(value.stringValue());
            sb.append("\n");
        }
        return sb.toString();
    }

    public static VtcMap parse(String serial) {
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
        return new VtcMap(builder.build());
    }

}
