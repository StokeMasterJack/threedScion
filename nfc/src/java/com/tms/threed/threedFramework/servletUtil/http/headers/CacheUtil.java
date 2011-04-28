package com.tms.threed.threedFramework.servletUtil.http.headers;

import javax.servlet.http.HttpServletResponse;

public class CacheUtil {


    /**
     *
     * These headers are stolen from Google Images. Specifically, it is based on this image of gwyneth Paltrow:
     *
     *      Request URL: http://t0.gstatic.com/images?q=tbn:ANd9GcTzDdPcEIoKnI3XLwWWvHoxmveWfo6k6djxSzM3Sz0XtVE8stsN
     *
     *       Response Headers:
     *           Content-Type: image/jpeg
     *           Last-Modified: Mon, 19 Apr 2010 22:39:21 GMT
     *           Date: Tue, 15 Feb 2011 16:12:02 GMT
     *           Expires: Wed, 15 Feb 2012 16:12:02 GMT
     *           Cache-Control: public, max-age=31536000
     *           X-Content-Type-Options: nosniff
     *           Server: sffe
     *           Content-Length: 7327
     *           X-XSS-Protection: 1; mode=block
     *
     * Adds the following headers to response:
     *      Cache-Control: public, max-age=31536000   (60 years in future)
     *      Expires: Wed, 15 Feb 2012 16:12:02 GMT   (1 year into future)
     *      Last-Modified: Mon, 19 Apr 2010 22:39:21 GMT  (passed in as arg)
     *
     * @param response
     */
    public static void addCacheForeverResponseHeaders(HttpServletResponse response) {

        //standard http

        CacheControl cc = new CacheControl();
        cc.setMaxAge(CacheControl.ONE_YEAR);  //31536000
        cc.setPublic(true);
        cc.addToResponse(response);

        Expires expires = Expires.expiresOneYearFromNow();
        expires.addToResponse(response);

        //Akamai Specific
        EdgeControl edgeControl = new EdgeControl();
        edgeControl.setMaxAge(EdgeControl.ONE_YEAR);
        edgeControl.setNoStore(false);
        edgeControl.setBypassCache(false);
        edgeControl.addToResponse(response);


    }

    public static void addCacheForXDaysResponseHeaders(HttpServletResponse response, int days) {

        CacheControl cc = new CacheControl();
        cc.setMaxAge(CacheControl.ONE_DAY * days);
        cc.setPublic(true);
        cc.addToResponse(response);

        Expires expires = Expires.expiresXDaysFromNow(days);
        expires.addToResponse(response);

         //Akamai Specific
        EdgeControl edgeControl = new EdgeControl();
        edgeControl.setMaxAge(EdgeControl.ONE_DAY * days);
        edgeControl.setNoStore(false);
        edgeControl.setBypassCache(false);
        edgeControl.addToResponse(response);

    }


    public static void addCacheNeverResponseHeaders(HttpServletResponse response) {
        Expires expires = Expires.expiresNow();
        expires.addToResponse(response);

        CacheControl cc1 = new CacheControl();
        cc1.setNoCache(true);
        cc1.setNoStore(true);
        cc1.setMaxAge(0);
        cc1.addToResponse(response);


        Pragma pragma = new Pragma();
        pragma.setNoCache(true);
        pragma.addToResponse(response);


        //Akamai Specific
        EdgeControl edgeControl = new EdgeControl();
        edgeControl.setNoStore(true);
        edgeControl.addToResponse(response);

    }


}
