package c3i.core.threedModel.client;

import com.google.gwt.core.client.JavaScriptObject;
import c3i.core.featureModel.client.JsFeatureModel;
import c3i.imageModel.client.JsImageModel;

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
