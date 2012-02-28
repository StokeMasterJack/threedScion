package com.tms.threed.threedFramework.util.servlet;

import com.tms.threed.threedFramework.util.servlet.http.RequestInterrogator;
import com.tms.threed.threedFramework.util.servlet.http.headers.CacheUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CacheFilter implements Filter {

    private ServletContext application;


    @Override public void init(FilterConfig config) throws ServletException {
        this.application = config.getServletContext();
    }

    @Override public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {


        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        RequestInterrogator r = new RequestInterrogator(request, response);

        chain.doFilter(request, response);

        if (r.isDotCacheFile()) {
            CacheUtil.addCacheForeverResponseHeaders(response);
        } else if (r.isDotNocacheFile()) {
            CacheUtil.addCacheForeverResponseHeaders(response);
        }

    }

    @Override public void destroy() {

    }
}
