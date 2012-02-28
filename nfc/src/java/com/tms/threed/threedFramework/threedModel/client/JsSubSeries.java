package com.tms.threed.threedFramework.threedModel.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.tms.threed.threedFramework.featureModel.client.JsFeatureModel;
import com.tms.threed.threedFramework.imageModel.client.JsImageModel;

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
