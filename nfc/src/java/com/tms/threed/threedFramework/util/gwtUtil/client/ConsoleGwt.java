package com.tms.threed.threedFramework.util.gwtUtil.client;

/**
 * Firebug logging
 */
public class ConsoleGwt {

    public static native void log(String msg) /*-{
        console.log(msg);
    }-*/;

    public static native void time(String msg) /*-{
        console.time(msg);
    }-*/;

    public static native void timeEnd(String msg) /*-{
        console.timeEnd(msg);
    }-*/;

    public static native void profile(String msg) /*-{
        console.profile(msg);
    }-*/;

    public static native void profileEnd(String msg) /*-{
        console.profileEnd(msg);
    }-*/;


    

}
