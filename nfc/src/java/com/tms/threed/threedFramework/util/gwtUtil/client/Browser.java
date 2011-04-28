package com.tms.threed.threedFramework.util.gwtUtil.client;

import com.google.gwt.user.client.Window;

public class Browser {

    private final String ua;
    private final boolean ie;
    private final boolean ie6;
    private final boolean webkit;
    private final boolean firefox;
    private final boolean opera;

    private static Browser INSTANCE;

    private Browser() {

        //init user agents
        String tmp = Window.Navigator.getUserAgent();
        if (tmp != null) {
            ua = tmp.toLowerCase();
        } else {
            ua = "unknown";
        }
        ie = ua.contains("msie");
        webkit = ua.contains("webkit") || ua.contains("safari");
        firefox = ua.contains("firefox");
        opera = ua.contains("opera");

        ie6 = ua.contains("msie 6");
//        ie6 = firefox;
    }

    public static Browser getInstance(){
        if(INSTANCE == null){
            INSTANCE = new Browser();
        }
        return INSTANCE;
    }

    public static boolean isIe() {
        return getInstance().ie;
    }

    public static boolean isIe6() {
//        return getInstance().gecko;
        return getInstance().ie6;
    }

    public static boolean isWebkit() {
        return getInstance().webkit;
    }

    public static boolean isFirefox() {
        return getInstance().firefox;
    }

    public static boolean isOpera() {
        return getInstance().opera;
    }

}
