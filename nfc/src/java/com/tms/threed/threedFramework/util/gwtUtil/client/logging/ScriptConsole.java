package com.tms.threed.threedFramework.util.gwtUtil.client.logging;

public class ScriptConsole {

    public native void log(String msg) /*-{
        console.log(msg);
    }-*/;

    public native void error(String msg) /*-{
        console.error(msg);
    }-*/;

    public native void time(String msg) /*-{
    }-*/;

    public native void timeEnd(String msg) /*-{
    }-*/;

    public native void profile(String msg) /*-{
    }-*/;

    public native void profileEnd(String msg) /*-{
    }-*/;


}
