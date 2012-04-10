package com.tms.threedToyota.byt.client.notification;

import com.google.gwt.core.client.JavaScriptObject;
import threed.core.threedModel.shared.BrandKey;
import threed.core.threedModel.shared.SeriesKey;

public final class JsAcc extends JavaScriptObject {

    protected JsAcc() {}

    private native String getBrand() /*-{
        return this.brand;
    }-*/;

    private native String getName() /*-{
        return this.name;
    }-*/;

    private native int getYear() /*-{
        return this.year;
    }-*/;

    public SeriesKey getSeriesKey() {
        int y = getYear();
        String n = getName();
        String b = getBrand();
        if(n == null) throw new IllegalStateException();
        if(b == null) throw new IllegalStateException();
        BrandKey brandKey = BrandKey.fromString(b);
        return new SeriesKey(brandKey,y, n);
    }

}
