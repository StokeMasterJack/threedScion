package threed.core.threedModel.client;

import com.google.gwt.core.client.JavaScriptObject;

public final class JsChatInfo extends JavaScriptObject {

    protected JsChatInfo() {}

    public native String getVehicleIconUrl() /*-{
        return this.vehicleIconUrl;
    }-*/;

    public native String getActionUrl() /*-{
        return this.actionUrl;
    }-*/;

}