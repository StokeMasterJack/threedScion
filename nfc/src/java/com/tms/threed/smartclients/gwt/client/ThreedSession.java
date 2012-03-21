package com.tms.threed.smartClients.gwt.client;

import com.google.common.collect.ImmutableSet;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.tms.threed.threedCore.threedModel.shared.SeriesKey;
import com.tms.threed.threedCore.threedModel.shared.Slice;
import com.tms.threed.threedCore.threedModel.shared.ViewKey;
import smartsoft.util.lang.shared.Path;

import java.util.List;

public interface ThreedSession {

    void setSeriesKey(SeriesKey seriesKey);

    List<ViewKey> getViewKeys();

    void setSlice(Slice slice);

    void setView(String viewName);

    String getView();

    void setAngle(int angle);

    int getAngle();

    void nextAngle();

    void previousAngle();

    void setPicksRaw(ImmutableSet<String> picks);

    List<Path> getUrls();

    HandlerRegistration addUrlChangeHandler(ValueChangeHandler<List<Path>> handler);


}