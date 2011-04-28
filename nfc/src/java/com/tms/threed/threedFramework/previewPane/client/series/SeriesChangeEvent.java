package com.tms.threed.threedFramework.previewPane.client.series;

import com.google.gwt.event.shared.GwtEvent;
import com.tms.threed.threedFramework.threedCore.shared.SeriesKey;

import static com.tms.threed.threedFramework.util.lang.shared.Strings.getSimpleName;

public class SeriesChangeEvent extends GwtEvent<SeriesChangeHandler> {

    private SeriesKey oldSeriesKey;
    private SeriesKey newSeriesKey;

    public static final Type<SeriesChangeHandler> TYPE = new Type<SeriesChangeHandler>();

    public SeriesChangeEvent(SeriesKey oldSeriesKey, SeriesKey newSeriesKey) {
        assert newSeriesKey != null;

        this.oldSeriesKey = oldSeriesKey;
        this.newSeriesKey = newSeriesKey;
    }

    @Override public Type<SeriesChangeHandler> getAssociatedType() {
        return TYPE;
    }

    @Override protected void dispatch(SeriesChangeHandler handler) {
        handler.onChange(this);
    }

    @Override public String toString() {
        return getSimpleName(this) + ": " + getNewSeriesKey();
    }

    public boolean isFirstTime() {
        return oldSeriesKey == null;
    }

    public SeriesKey getOldSeriesKey() {
        return oldSeriesKey;
    }

    public SeriesKey getNewSeriesKey() {
        return newSeriesKey;
    }
}