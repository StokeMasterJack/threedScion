package com.tms.threed.smartClients.gwt.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.tms.threed.threedCore.threedModel.client.JsSeriesId;
import smartsoft.util.lang.shared.Path;

import java.util.List;

public interface ThreedSessionForJs {

    void setSeriesKey(int year, String seriesName);

    void setSlice(String viewName, int angle);

    void setPicks(JsArrayString picks);

    void setView(String viewName);

    String getView();

    void setAngle(int angle);

    int getAngle();

    void nextAngle();

    void previousAngle();

    JsArrayString getUrls();

    JsArrayString getViews();

    void setSeriesIdDude(JsSeriesId jsSeriesId);

    void addUrlChangeHandler(JavaScriptObject handler);

    class MyValChHandler implements ValueChangeHandler<List<Path>> {

        public MyValChHandler() {

        }

        @Override
        public void onValueChange(ValueChangeEvent<List<Path>> event) {

        }
    }


}