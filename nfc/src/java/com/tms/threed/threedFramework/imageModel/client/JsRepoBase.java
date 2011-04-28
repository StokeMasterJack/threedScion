package com.tms.threed.threedFramework.imageModel.client;

import com.google.gwt.core.client.JavaScriptObject;

public final class JsRepoBase extends JavaScriptObject {

    protected JsRepoBase() {}

    public native String getDir() /*-{
        return this.dir;
    }-*/;

    public native String getUrl() /*-{
       return this.url;
    }-*/;


}
