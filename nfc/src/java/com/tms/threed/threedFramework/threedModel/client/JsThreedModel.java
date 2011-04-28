package com.tms.threed.threedFramework.threedModel.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.tms.threed.threedFramework.featureModel.client.JsFeatureModel;
import com.tms.threed.threedFramework.imageModel.client.JsImageModel;

public final class JsThreedModel extends JavaScriptObject {

    protected JsThreedModel() {}

    public native JsFeatureModel getJsFeatureModel() /*-{
        return this.featureModel;
    }-*/;

    public native JsImageModel getJsImageModel() /*-{
        return this.imageModel;
    }-*/;


}
