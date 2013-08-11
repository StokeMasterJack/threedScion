package c3i.core.threedModel.client;

import com.google.gwt.core.client.JavaScriptObject;

public final class JsThreedConfig extends JavaScriptObject {

    protected JsThreedConfig() {}

    public native String getThreedRootHttp() /*-{
        return this.threedRootHttp;
    }-*/;

    public native String getThreedRootFileSystem() /*-{
        return this.threedRootFileSystem;
    }-*/;

     public native String getPngRootHttp() /*-{
        return this.pngRootHttp;
    }-*/;

    public native String getJpgRootHttp() /*-{
        return this.jpgRootHttp;
    }-*/;

}