package com.tms.threed.threedFramework.imageModel.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;

public final class JsImageModel extends JavaScriptObject {

    protected JsImageModel() {}

    public native JSONArray getViews() /*-{
        var v = this.views;
        var func = @com.google.gwt.json.client.JSONParser::typeMap[typeof v];
        return func ? func(v) : @com.google.gwt.json.client.JSONParser::throwUnknownTypeException(Ljava/lang/String;)(typeof v);
    }-*/;

    public native JsRepoBase getRepoBase() /*-{
       return this.repoBase;
    }-*/;


}
