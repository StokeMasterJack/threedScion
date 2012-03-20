package com.tms.threed.threedCore.threedModel.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.tms.threed.threedCore.featureModel.client.JsFeatureModel;
import com.tms.threed.threedCore.imageModel.client.JsImageModel;

public final class JsThreedModel extends JavaScriptObject {

    protected JsThreedModel() {}

    public native JsFeatureModel getJsFeatureModel() /*-{
        return this.featureModel;
    }-*/;

    public native JsImageModel getJsImageModel() /*-{
        return this.imageModel;
    }-*/;


}
