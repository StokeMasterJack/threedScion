package com.tms.threed.threedFramework.util.servlet.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class HttpUtil {


    /**
     *  Not 100% accurate, this based on mime type (and/or file extensions)
     * that are <i>typically</i> static: images, css, js, favicon
     */
    public static boolean isStaticResource(HttpServletRequest request) {
        if(isResourceImage(request)) return true;
        if(isResourceCss(request)) return true;
        if(isResourceJavaScript(request)) return true;
        if(isResourceFavicon(request)) return true;
        return false;
    }


    public static boolean isResourceImage(HttpServletRequest request) {
        String mimeType = request.getContentType();
        if (mimeType == null) {
            return false;
        }
        else if (mimeType.startsWith("image/")) {
            return true;
        }
        else {
            String uri = HttpUtil.getContextRelativeRequestUri(request);
            if (uri == null) {
                return false;
            }
            else {
                if (uri.endsWith(".gif")) return true;
                else if (uri.endsWith(".jpg")) return true;
                else if (uri.endsWith(".png")) return true;
                else if (uri.endsWith(".ico")) return true;
                else return false;
            }
        }
    }

    public static boolean hasMimeType(HttpServletRequest request, String mimeType) {
        if (mimeType == null)
            throw new IllegalArgumentException("\"mimeType\" is a required argument to method HttpUtil.isMimeType(..)");
        if (request == null) return false;
        String actualMimeType = request.getContentType();
        if (actualMimeType == null) return false;
        return actualMimeType.equalsIgnoreCase(mimeType);
    }

    /**
     * @param fileExtension example: "html" or "gif" (do not include the dot)
     */
    public static boolean hasExtension(HttpServletRequest request, String fileExtension) {
        if (fileExtension == null) throw new IllegalArgumentException("\"fileExtension\" is required");
        String uri = HttpUtil.getContextRelativeRequestUri(request);
        if (uri == null) return false;
        if (!fileExtension.startsWith("")) fileExtension = "" + fileExtension;
        return uri.endsWith(fileExtension);
    }

    public static boolean isResourceCss(HttpServletRequest request) {
        if (hasMimeType(request, "text/css")) return true;
        if (hasExtension(request, "css")) return true;
        else return false;
    }

    public static boolean isResourceJavaScript(HttpServletRequest request) {
        if (hasMimeType(request, "text/javascript")) return true;
        if (hasExtension(request, "js")) return true;
        else return false;
    }

    public static boolean isResourceFavicon(HttpServletRequest request) {
        String uri = HttpUtil.getContextRelativeRequestUri(request);
        if (uri == null) return false;
        return uri.endsWith(".favicon.ico");
    }

    public static String getContextRelativeRequestUri(HttpServletRequest request) {
        if (request == null) throw new NullPointerException();
        String cp = request.getContextPath();
        if (cp == null) return request.getRequestURI();
        int L = cp.length();
        return request.getRequestURI().substring(L);
    }

    public static String getHttpGmtString(Date date) {
        TimeZone gmt = TimeZone.getTimeZone("GMT");
        DateFormat dateTimeInstance = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        dateTimeInstance.setTimeZone(gmt);
        String gmtString = dateTimeInstance.format(date);
        return gmtString;
    }

    public static String getHttpGmtString() {
        return getHttpGmtString(new Date());
    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static String urlDecode(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static String getRealRequestPath(HttpServletRequest request) {
        String contextRelativeRequestUri = getContextRelativeRequestUri(request);
        return request.getSession().getServletContext().getRealPath(contextRelativeRequestUri);
    }

    /**
     * @return If this requestUri refers to a valid file or directory 
     * return that file (or dir) else return null
     */
    public static File getRealFile(HttpServletRequest request) {
        String realPath = getRealRequestPath(request);
        File f = new File(realPath);
        if (!f.exists()) {
            return null;
        }
        else {
            return f;
        }
    }

    public static File getRealContextPath(HttpServletRequest request){
        ServletContext a = request.getSession().getServletContext();
        return new File(a.getRealPath("/"));
    }


    
}
