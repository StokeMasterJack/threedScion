package c3i.threedModel.client;

import c3i.featureModel.client.JsFeatureModel;
import c3i.imageModel.client.JsImageModel;
import com.google.gwt.core.client.JavaScriptObject;

public final class JsThreedModel extends JavaScriptObject {

    protected JsThreedModel() {
    }

    public native JsFeatureModel getJsFeatureModel() /*-{
        return this.featureModel;
    }-*/;

    public native JsImageModel getJsImageModel() /*-{
        return this.imageModel;
    }-*/;

}
