package com.tms.threed.threedFramework.servletUtil.http.headers;

import com.tms.threed.threedFramework.servletUtil.http.HttpUtil;
import com.tms.threed.threedFramework.util.lang.server.date.Date;

import javax.servlet.http.HttpServletResponse;

public class Expires {


    private String headerValue;

    public Expires() {
        this(new Date().addMonths(1));
    }

    public Expires(int year, int month, int dayOfMonth) {
        this(new Date(year, month, dayOfMonth));
    }

    public Expires(Date expiresDate) {
        java.util.Date utilDate = expiresDate.toUtilDate();
        headerValue = HttpUtil.getHttpGmtString(utilDate);
    }

    public String getHeaderName() {
        return "Expires";
    }

    public String getHeaderValue() {
        return headerValue;
    }

    public String getHeader() {
        return getHeaderName() + ": " + getHeaderValue();
    }


    public static Expires expiresOneMonthsFromNow() {
        return expiresXMonthsFromNow(1);
    }

    public static Expires expiresOneYearFromNow() {
        return expiresXMonthsFromNow(12);
    }

    public static Expires expiresXMonthsFromNow(int monthFromNow) {
        Date today = new Date();
        return new Expires(today.addMonths(monthFromNow));
    }

    public static Expires expiresXDaysFromNow(int daysFromNow) {
        Date today = new Date();
        return new Expires(today.addDays(daysFromNow));
    }

    public static Expires expiresLastWeek() {
        Date today = new Date();
        return new Expires(today.addDays(-7));
    }

     public static Expires expiresNow() {
        Date today = new Date();
        return new Expires(today);
    }

    @Override public String toString() {
        return getHeader();
    }

    public void addToResponse(HttpServletResponse response) {
        response.setHeader(getHeaderName(), getHeaderValue());
    }
}
