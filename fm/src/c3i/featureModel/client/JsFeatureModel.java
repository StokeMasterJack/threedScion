package c3i.featureModel.client;

import com.google.gwt.core.client.JavaScriptObject;

public final class JsFeatureModel extends JavaScriptObject {

    protected JsFeatureModel() {
    }

    public native JsVar getRootVar() /*-{
        return this.rootVar;
    }-*/;

    public native String getDisplayName() /*-{
        return this.displayName;
    }-*/;

    public native String getBrand() /*-{
        return this.brand;
    }-*/;

    public native String getName() /*-{
        return this.name;
    }-*/;

    public native int getYear() /*-{
        return this.year;
    }-*/;

    public native JsBoolExpr getConstraints() /*-{
        return this.constraints;
    }-*/;


}