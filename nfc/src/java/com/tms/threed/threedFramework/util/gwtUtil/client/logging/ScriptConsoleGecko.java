package com.tms.threed.threedFramework.util.gwtUtil.client.logging;

public class ScriptConsoleGecko extends ScriptConsole {

    public native void time(String msg) /*-{
        console.time(msg);
    }-*/;

    public native void timeEnd(String msg) /*-{
        console.timeEnd(msg);
    }-*/;

    public native void profile(String msg) /*-{
        console.profile(msg);
    }-*/;

    public native void profileEnd(String msg) /*-{
        console.profileEnd(msg);
    }-*/;
}