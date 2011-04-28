package com.tms.threed.threedFramework.threedCore.shared;

import com.tms.threed.threedFramework.repo.shared.RootTreeId;

import javax.annotation.Nonnull;

public class SeriesId {

    private final SeriesKey seriesKey;
    private final RootTreeId rootTreeId; //full 40 digit sha

    /**
     *
     * @param seriesKey
     * @param rootTreeId git commit sha
     */
    public SeriesId(@Nonnull SeriesKey seriesKey, @Nonnull RootTreeId rootTreeId) {
        assert seriesKey != null;
        assert rootTreeId != null;

        this.seriesKey = seriesKey;
        this.rootTreeId = rootTreeId;
    }

    public SeriesId(@Nonnull String seriesName,int seriesYear, @Nonnull String rootTreeId) {
        this(new SeriesKey(seriesYear, seriesName), new RootTreeId(rootTreeId));
    }

    public SeriesKey getSeriesKey() {
        return seriesKey;
    }

    public RootTreeId getRootTreeId() {
        return rootTreeId;
    }

    public String getName() {
        return seriesKey.getName();
    }

    public int getYear() {
        return seriesKey.getYear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SeriesId id = (SeriesId) o;

        if (!rootTreeId.equals(id.rootTreeId)) return false;
        if (!seriesKey.equals(id.seriesKey)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = seriesKey != null ? seriesKey.hashCode() : 0;
        result = 31 * result + (rootTreeId != null ? rootTreeId.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return seriesKey.toString() + " " + rootTreeId;
    }

}
