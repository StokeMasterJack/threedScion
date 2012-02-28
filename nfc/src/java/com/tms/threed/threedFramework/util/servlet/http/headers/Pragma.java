package com.tms.threed.threedFramework.util.servlet.http.headers;

import com.tms.threed.threedFramework.util.lang.server.StringUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

/*
*   no-cache
*/


public class Pragma {

    public static final String HEADER_NAME = "Pragma";

    private Boolean noCache = null;

    public Pragma() {
    }

    public Boolean getNoCache() {
        return noCache;
    }

    public void setNoCache(Boolean noCache) {
        this.noCache = noCache;
    }


    public String getHeaderValue() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter out = new PrintWriter(stringWriter);
        if (noCache != null && noCache)
            out.print("no-cache");
        out.flush();
        String headerValue = stringWriter.toString().trim();
        headerValue = StringUtil.chompTrailingComma(headerValue);
        //System.out.println("headerValue = " + headerValue);
        return headerValue;
    }

    public String getHeaderName() {
        return HEADER_NAME;
    }

    public String getHeader() {
        return getHeaderName() + ": " + getHeaderValue();
    }

    public String toString() {
        return getHeader();
    }

    public void addToResponse(HttpServletResponse response) {
        if (noCache != null && noCache) {
            response.setHeader(getHeaderName(), getHeaderValue());
        }
    }
}
