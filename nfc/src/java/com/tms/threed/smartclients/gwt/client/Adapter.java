package com.tms.threed.smartClients.gwt.client;

public class Adapter {

    public static void registerHooks(ThreedSession s) {
        ThreedSessionForJs ts = new ThreedSessionForJsImpl(s);
        registerHooks2(ts);
    }

    private static native void registerHooks2(ThreedSessionForJs s)/*-{

        var o = {};

        o.setSeriesKey = function (seriesYear, seriesName) {
            s.@com.tms.threed.smartClients.gwt.client.ThreedSessionForJs::setSeriesKey(ILjava/lang/String;)(seriesYear, seriesName);
        };

        o.setSlice = function (viewName, angle) {
            s.@com.tms.threed.smartClients.gwt.client.ThreedSessionForJs::setSlice(Ljava/lang/String;I)(viewName, angle);
        };

        o.setPicks = function (picks) {
            s.@com.tms.threed.smartClients.gwt.client.ThreedSessionForJs::setPicks(Lcom/google/gwt/core/client/JsArrayString;)(picks);
        };

        o.addUrlChangeHandler = function (changeHandler) {
            s.@com.tms.threed.smartClients.gwt.client.ThreedSessionForJs::addUrlChangeHandler(Lcom/google/gwt/core/client/JavaScriptObject;)(changeHandler);
        };

        o.getUrls = function () {
            return s.@com.tms.threed.smartClients.gwt.client.ThreedSessionForJs::getUrls()();
        };

        o.getViews = function () {
            return s.@com.tms.threed.smartClients.gwt.client.ThreedSessionForJs::getViews()();
        };

        o.nextAngle = function () {
            s.@com.tms.threed.smartClients.gwt.client.ThreedSessionForJs::nextAngle()();
        };

        o.previousAngle = function () {
            s.@com.tms.threed.smartClients.gwt.client.ThreedSessionForJs::previousAngle()();
        };

        o.setView = function (viewName) {
            s.@com.tms.threed.smartClients.gwt.client.ThreedSessionForJs::setView(Ljava/lang/String;)(viewName);
        };

        o.setAngle = function (angle) {
            return s.@com.tms.threed.smartClients.gwt.client.ThreedSessionForJs::setAngle(I)(angle);
        };

        o.getView = function () {
            return s.@com.tms.threed.smartClients.gwt.client.ThreedSessionForJs::getView()();
        };

        o.getAngle = function () {
            return s.@com.tms.threed.smartClients.gwt.client.ThreedSessionForJs::getAngle()();
        };

        if ($wnd.initThreedSession) {
            $wnd.initThreedSession(o);
        }

    }-*/;
}
