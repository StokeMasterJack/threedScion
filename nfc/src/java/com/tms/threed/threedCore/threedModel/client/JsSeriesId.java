package com.tms.threed.threedCore.threedModel.client;

import com.google.gwt.core.client.JavaScriptObject;

public final class JsSeriesId extends JavaScriptObject {

    protected JsSeriesId() {}

    public native String getName() /*-{
        return this.name;
    }-*/;

    public native int getYear() /*-{
        return this.year;
    }-*/;

   


}