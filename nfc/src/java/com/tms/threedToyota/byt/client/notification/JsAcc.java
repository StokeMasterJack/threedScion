package com.tms.threedToyota.byt.client.notification;

import com.google.gwt.core.client.JavaScriptObject;
import com.tms.threed.threedFramework.threedModel.shared.SeriesKey;

public final class JsAcc extends JavaScriptObject {

    protected JsAcc() {}

    private native String getName() /*-{
        return this.name;
    }-*/;

    private native int getYear() /*-{
        return this.year;
    }-*/;

    public SeriesKey getSeriesKey() {
        int y = getYear();
        String n = getName();
        if(n == null) throw new IllegalStateException();
        return new SeriesKey(y, n);
    }

}
