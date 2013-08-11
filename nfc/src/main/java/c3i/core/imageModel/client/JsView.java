package c3i.core.imageModel.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public final class JsView extends JavaScriptObject {

    protected JsView() {}

    public native String getName() /*-{
        return this.name;
    }-*/;

    public native JsArray<JsLayer> getLayers() /*-{
       return this.layers;
    }-*/;


}
