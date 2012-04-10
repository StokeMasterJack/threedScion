package threed.core.threedModel.client;

import com.google.gwt.core.client.JavaScriptObject;
import threed.core.featureModel.client.JsFeatureModel;
import threed.core.imageModel.client.JsImageModel;

public final class JsThreedModel extends JavaScriptObject {

    protected JsThreedModel() {}

    public native JsFeatureModel getJsFeatureModel() /*-{
        return this.featureModel;
    }-*/;

    public native JsImageModel getJsImageModel() /*-{
        return this.imageModel;
    }-*/;


}
