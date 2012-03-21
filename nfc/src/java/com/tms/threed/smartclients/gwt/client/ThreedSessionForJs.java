package com.tms.threed.smartClients.gwt.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.tms.threed.threedCore.threedModel.client.JsSeriesId;

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

    void addUrlChangeHandler(JavaScriptObject handler);

}