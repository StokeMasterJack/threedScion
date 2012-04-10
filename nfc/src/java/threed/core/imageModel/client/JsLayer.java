package threed.core.imageModel.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public final class JsLayer extends JavaScriptObject {

    protected JsLayer() {}

    public native String getName() /*-{
        return this.name;
    }-*/;

    public native JsArray<JsLayer> getChildNode() /*-{
       return this.layers;
    }-*/;


}
