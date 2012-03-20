package com.tms.threed.threedCore.threedModel.client;

import com.google.gwt.core.client.JavaScriptObject;

public final class JsSubSeries extends JavaScriptObject {

    protected JsSubSeries() {}

    public native String getLabel() /*-{
        return this.label;
    }-*/;

    public native int getYear() /*-{
        if(this.year && this.year != null){
            return this.year;
        }else{
            return -1;
        }
    }-*/;


}
