package com.tms.threed.threedFramework.util.servlet.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class RespDec extends HttpServletResponseWrapper {

    private HttpServletRequest request;

    public RespDec(HttpServletResponse response, HttpServletRequest request) {
        super(response);
        this.request = request;

    }

    @Override public void setHeader(String name, String value) {
//        System.out.println("SetHeader: " + name + ": " + value);
        super.setHeader(name, value);
    }

    @Override public void addHeader(String name, String value) {
//        System.out.println("addHeader: " + name + ": " + value);
        super.addHeader(name, value);
    }


}
