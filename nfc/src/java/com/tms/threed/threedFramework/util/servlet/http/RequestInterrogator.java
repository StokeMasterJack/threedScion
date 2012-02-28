package com.tms.threed.threedFramework.util.servlet.http;

import com.tms.threed.threedFramework.util.servlet.http.headers.CacheUtil;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RequestInterrogator {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
//    private final File repoBaseDir;



    private final String requestUri;
    private final String contextPath;

    private final String waRelativeRequestUri;
    public static final Map<String, String> mimeMap = new HashMap<String, String>();

    private boolean gc;
    private boolean gn;
    private boolean vb;

    private boolean cacheForever;
    private boolean cacheNever;

    public RequestInterrogator(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.requestUri = request.getRequestURI();

        this.contextPath = request.getContextPath();

        this.waRelativeRequestUri = requestUri.substring(contextPath.length());



        gc = isDotCacheFile();
        gn = isDotNocacheFile();


        vb = isButtonImage();

        cacheForever = gc || vb;

        cacheNever = gn;


//        printRequestSummary();
    }

    boolean isStaticFileInternalToWar() {
        return gc || gn;
    }


    public void printRequestSummary() {
        System.out.println("START: ==============");
        System.out.println(requestUri);

        System.out.println("\t serverName: " + request.getServerName());
        System.out.println("\t contextPath: " + contextPath);
        System.out.println("\t waRelativeRequestUriX: " + waRelativeRequestUri);
        System.out.println("\t RealFileForVersionedThreedImage: " + getRealFileForVersionedThreedImage());
        System.out.println("\t isButtonImage: " + isButtonImage());
        System.out.println("\t isGwtDotCacheFile: " + isDotCacheFile());
        System.out.println("\t isGwtDotNocacheFile: " + isDotNocacheFile());
        System.out.println("\t getCacheCategory: " + getCacheCategory());
        System.out.println("\t getCacheCategory: " + getCacheCategory());
        System.out.println("END: ==============");
        System.out.println();
    }

    public String getCacheCategory() {
        if (gc || vb) {
            return "cacheForever";
        } else if (gn) {
            return "dontCacheAtAll";
        } else {
            return "other";
        }
    }

    public void addCacheHeadersToResponse() {
        if (cacheForever) {
            CacheUtil.addCacheForeverResponseHeaders(response);
        } else if (cacheNever) {
            CacheUtil.addCacheNeverResponseHeaders(response);
        }
    }

    public boolean isDotCacheFile() {
        return requestUri != null && requestUri.contains(".cache.");
    }

    public boolean isDotNocacheFile() {
        return requestUri != null && requestUri.contains(".nocache.");
    }

    public boolean isButtonImage() {
        boolean aa = requestUri.endsWith(".png");
        boolean bb = requestUri.contains("-v");
        boolean cc = requestUri.contains("previewPanel");
        boolean dd = requestUri.contains("angleButtons");
        return aa && bb && cc && dd;
    }

    private File getRealFileForVersionedThreedImage() {
        ServletContext application = request.getSession().getServletContext();
        String result = application.getRealPath(waRelativeRequestUri);

        if (request.getServerName().contains("ssdev.toyota.com")) {
            result = "c:/Data/tmsConfig" + waRelativeRequestUri;
        }

        return new File(result);
    }

    private File getRealFileForWarResource() {
        ServletContext application = request.getSession().getServletContext();
        String result = application.getRealPath(waRelativeRequestUri);
        return new File(result);
    }

    public static String getFileExtension(String requestUri) {
        if (requestUri == null) return null;
        int lastDot = requestUri.lastIndexOf("");
        return requestUri.substring(lastDot + 1);
    }

    public String getFileExtension() {
        return getFileExtension(requestUri);
    }

    public String getMimeType() {
        String ext = getFileExtension();
        String mimeType = mimeMap.get(ext);
        if (mimeType == null) {
            throw new IllegalStateException("Unable to guess mime-type from extension[" + ext + "] of url[" + requestUri + "]");
        } else {
            return mimeType;
        }
    }

    static {
        mimeMap.put("gif", "image/gif");
        mimeMap.put("jsp", "text/html");
        mimeMap.put("jpg", "image/jpeg");
        mimeMap.put("png", "image/png");
        mimeMap.put("html", "text/html");
        mimeMap.put("htm", "text/html");
        mimeMap.put("js", "application/x-javascript");
        mimeMap.put("css", "text/css");
    }


}
