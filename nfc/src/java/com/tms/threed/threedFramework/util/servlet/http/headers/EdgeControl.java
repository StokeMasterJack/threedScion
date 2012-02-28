package com.tms.threed.threedFramework.util.servlet.http.headers;

import com.tms.threed.threedFramework.util.lang.server.StringUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

/*

* Akamai specific
*    max-age=[seconds]
*       specifies the maximum amount
*       of time that an representation will be
*       considered fresh by Akamia.
*       [seconds] is the number of seconds from the time of
*       the request you wish the representation to be fresh for.
*
*   no-store
*       instructs Akamai not to keep a copy of the representation
*       under any conditions.
*

*/


public class EdgeControl {

    public static final String HEADER_NAME = "Edge-control";

    public static final int ONE_MINUTE = 60;
    public static final int HALF_HOUR = 30 * ONE_MINUTE;
    public static final int ONE_HOUR = 60 * ONE_MINUTE;
    public static final int ONE_DAY = ONE_HOUR * 24;
    public static final int ONE_WEEK = ONE_DAY * 7;
    public static final int ONE_MONTH = ONE_DAY * 30;
    public static final int ONE_YEAR = ONE_DAY * 365;    //31536000 seconds

    private Integer maxAge = null; //in seconds
    private Boolean noStore = null;
    private Boolean bypassCache = null;


    public EdgeControl() {
    }


    /**
     *
     * @return maxAge in seconds
     */
    public Integer getMaxAge() {
        return maxAge;
    }

    /**
     * @param maxAgeInSeconds in seconds
     */
    public void setMaxAge(Integer maxAgeInSeconds) {
        this.maxAge = maxAgeInSeconds;
    }

    public void setMaxAgeInMonths(Integer maxAgeInMonths) {
        this.maxAge = ONE_MONTH * maxAgeInMonths;
    }

    public Boolean getNoStore() {
        return noStore;
    }

    public void setNoStore(Boolean noStore) {
        this.noStore = noStore;
    }

    public Boolean getBypassCache() {
        return bypassCache;
    }

    public void setBypassCache(Boolean bypassCache) {
        this.bypassCache = bypassCache;
    }

    public String getHeaderValue() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter out = new PrintWriter(stringWriter);

        if (maxAge != null) {
            out.print("max-age=" + maxAge + ",");
        }

        if (noStore != null) {
            if (noStore) {
                out.print("no-store,");
            } else {
                out.print("!no-store,");
            }
        }

         if (bypassCache != null) {
            if (bypassCache) {
                out.print("bypass-cache, ");
            } else {
                out.print("!bypass-cache, ");
            }
        }

        out.flush();
        String headerValue = stringWriter.toString().trim();
        headerValue = StringUtil.chompTrailingComma(headerValue);
//        System.out.println("headerValue = " + headerValue);
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
        response.setHeader(getHeaderName(), getHeaderValue());
    }
}
