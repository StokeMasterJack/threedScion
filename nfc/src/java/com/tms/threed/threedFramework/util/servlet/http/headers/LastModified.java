package com.tms.threed.threedFramework.util.servlet.http.headers;

import com.tms.threed.threedFramework.util.servlet.http.HttpUtil;
import com.tms.threed.threedFramework.util.lang.server.date.Date;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

public class LastModified {

    private String headerValue;
    private java.util.Date lastModifiedUtilDate;

    public LastModified(Date d) {
        long lastModified = d.toUtilDate().getTime();
        this.lastModifiedUtilDate = new java.util.Date(lastModified);
        headerValue = HttpUtil.getHttpGmtString(lastModifiedUtilDate);
    }

    public LastModified(long lastModifiedMillis) {
        lastModifiedUtilDate = new java.util.Date(lastModifiedMillis);
        headerValue = HttpUtil.getHttpGmtString(lastModifiedUtilDate);
    }

    public LastModified(File file) {
        long lastModified = file.lastModified();
        this.lastModifiedUtilDate = new java.util.Date(lastModified);
        headerValue = HttpUtil.getHttpGmtString(lastModifiedUtilDate);
    }

    public String getHeaderName() {
        return "Last-Modified";
    }

    public String getHeaderValue() {
        return headerValue;
    }

    public String getHeader() {
        return getHeaderName() + ": " + getHeaderValue();
    }

    public java.util.Date getValueAsUtilDate() {
        return lastModifiedUtilDate;
    }

    @Override public String toString() {
        return getHeader();
    }

    public void addToResponse(HttpServletResponse response) {
        response.setHeader(getHeaderName(), getHeaderValue());
    }
}
