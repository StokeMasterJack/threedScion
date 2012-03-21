package com.tms.threed.smartClients.gwt.client;

import com.google.common.collect.ImmutableSet;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.tms.threed.threedCore.threedModel.client.JsSeriesId;
import com.tms.threed.threedCore.threedModel.shared.SeriesKey;
import com.tms.threed.threedCore.threedModel.shared.Slice;
import com.tms.threed.threedCore.threedModel.shared.ViewKey;
import smartsoft.util.lang.shared.Path;

import java.util.List;

public class ThreedSessionForJsImpl implements ThreedSessionForJs {

    private final ThreedSession threedSession;

    public ThreedSessionForJsImpl(ThreedSession threedSession) {
        this.threedSession = threedSession;
    }


    @Override
    public void setSeriesKey(int year, String seriesName) {
        threedSession.setSeriesKey(new SeriesKey(year, seriesName));
    }

    @Override
    public void setSlice(String viewName, int angle) {
        threedSession.setSlice(new Slice(viewName, angle));
    }

    @Override
    public void setPicks(JsArrayString picks) {
        ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        for (int i = 0; i < picks.length(); i++) {
            String s = picks.get(i);
            builder.add(s);
        }
        threedSession.setPicksRaw(builder.build());
    }

    @Override
    public JsArrayString getViews() {
        JavaScriptObject array = JavaScriptObject.createArray();
        JsArrayString a = array.cast();
        for (ViewKey viewKey : threedSession.getViewKeys()) {
            a.push(viewKey.getName());
        }
        return a;
    }

    @Override
    public JsArrayString getUrls() {
        JavaScriptObject array = JavaScriptObject.createArray();
        JsArrayString a = array.cast();
        for (Path path : threedSession.getUrls()) {
            a.push(path.toString());
        }
        return a;
    }

    @Override
    public void addUrlChangeHandler(JavaScriptObject handler) {
        MyHandler myHandler = new MyHandler(handler);
        threedSession.addUrlChangeHandler(myHandler);
    }

    @Override
    public void setView(String viewName) {
        threedSession.setView(viewName);
    }

    @Override
    public void setAngle(int angle) {
        threedSession.setAngle(angle);
    }

    @Override
    public String getView() {
        return threedSession.getView();
    }

    @Override
    public int getAngle() {
        return threedSession.getAngle();
    }

    @Override
    public void nextAngle() {
        threedSession.nextAngle();
    }

    @Override
    public void previousAngle() {
        threedSession.previousAngle();
    }

    class MyHandler implements ValueChangeHandler<List<Path>> {

        JavaScriptObject jsFunction;

        MyHandler(JavaScriptObject jsFunction) {
            this.jsFunction = jsFunction;
        }

        @Override
        public void onValueChange(ValueChangeEvent<List<Path>> event) {
            apply(jsFunction, this);
        }

        private native void apply(Object jsFunction, Object thisObj) /*-{
            var args = [];
            if (@com.google.gwt.core.client.GWT::isScript()()) {
                return jsFunction.apply(thisObj, args);
            } else {
                _ = jsFunction.apply(thisObj, args);
                if (_ != null) {
                    // Wrap for Development Mode
                    _ = Object(_);
                }
                return _;
            }
        }-*/;


    }

    public void setSeriesIdDude(JsSeriesId jsSeriesId) {
        String name = jsSeriesId.getName();
        Window.alert("hello same: " + name);
    }


}
