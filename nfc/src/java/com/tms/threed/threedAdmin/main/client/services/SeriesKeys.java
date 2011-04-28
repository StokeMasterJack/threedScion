package com.tms.threed.threedAdmin.main.client.services;

import com.tms.threed.threedFramework.threedCore.shared.SeriesKey;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class SeriesKeys {

    private final List<SeriesKey> seriesKeys;

    public SeriesKeys(List<SeriesKey> seriesKeys) {
        this.seriesKeys = seriesKeys;
    }

    public SortedSet<Integer> getYears() {
        SortedSet<Integer> set = new TreeSet<Integer>();
        for (SeriesKey seriesKey : seriesKeys) {
            set.add(seriesKey.getYear());
        }
        return set;
    }

    public SortedSet<SeriesKey> getSeriesKeys(int year) {
        SortedSet<SeriesKey> set = new TreeSet<SeriesKey>();
        for (SeriesKey seriesKey : seriesKeys) {
            if (seriesKey.getYear() == year) {
                set.add(seriesKey);
            }
        }
        return set;
    }
}
