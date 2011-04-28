package com.tms.threed.threedFramework.util.date.shared;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class GwtUtil {

    public static void setBackgroundColor(Widget w, String color) {
        w.getElement().getStyle().setProperty("background-color", color);
    }

    public static void setBackgroundRed(Widget w) {
        setBackgroundColor(w, "red");
    }

    public static int emsToPixels(int ems) {
        if(ems == -1) return -1;
        return ems * 10;
    }

    public static class Offset {
        public int top;
        public int left;
    }

    public static Offset getAbsoluteOffset(Element forElement) {
        Offset offset = new Offset();

        Element element = forElement;
        while (true) {
            offset.left += element.getOffsetLeft();
            offset.top += element.getOffsetTop();
            if (isOffsetParentBody(element) && isAbsolute(element)) break;  // Safari fix
            if (element.getOffsetParent() == null) break;
            element = (Element) element.getOffsetParent();
        }
        element = forElement;
        while (true) {
            if (getUserAgent().equals("opera") || (element.getTagName() != null && (element.getTagName().toUpperCase() == "BODY"))) {
                offset.left -= element.getScrollLeft();
                offset.top -= element.getScrollTop();
            }

            if (element.getParentNode() == null) break;
            element = (Element) element.getParentNode();


        }

        offset.left += getClientOffsetLeft();
        offset.top += getClientOffsetTop();

        return offset;
    }


    private static boolean isAbsolute(Element element) {
        return element.getStyle().getProperty("position").equals("absolute");
    }

    public static native boolean isOffsetParentBody(Element element) /*-{
        return element.offsetParent == $doc.body;
    }-*/;


    public static native int getClientOffsetLeft() /*-{
        return $doc.documentElement.clientLeft || $doc.body.clientLeft;
    }-*/;

    public static native int getClientOffsetTop() /*-{
        return $doc.documentElement.clientTop || $doc.body.clientTop;
    }-*/;


    public static native String getUserAgent() /*-{
                var ua = navigator.userAgent.toLowerCase();
                if (ua.indexOf("opera") != -1) {
                        return "opera";
                }
                if (ua.indexOf("webkit") != -1) {
                        return "safari";
                }
                if ((ua.indexOf("msie 6.0") != -1)
                ||  (ua.indexOf("msie 7.0") != -1)) {
                        return "ie6";
                }
                if (ua.indexOf("gecko") != -1) {
                        var result = /rv:([0-9]+)\.([0-9]+)/.exec(ua);
                        if (result && result.length == 3) {
                                var version = (parseInt(result[1]) * 10) + parseInt(result[2]);
                                if (version >= 18)
                                        return "gecko1_8";
                        }
                        return "gecko";
                }
                return "unknown";
        }-*/;


    public static String getSimpleName(Class cls) {
        String className = cls.getName();
        String[] a = className.split("\\.");
        return a[a.length - 1];     //AccountDef
    }

   

    
    
}
