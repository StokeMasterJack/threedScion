package com.tms.threed.threedFramework.threedAdmin.main.server;

import com.tms.threed.threedFramework.util.servlet.http.RequestInterrogator;
import com.tms.threed.threedFramework.util.servlet.http.headers.CacheUtil;
import com.tms.threed.threedFramework.util.config.ConfigHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ThreedAdminFilter implements Filter {

    private static Log log;

    static {
        ConfigHelper.maybeInitLogger(ThreedAdminFilter.class.getSimpleName());
        log = LogFactory.getLog(ThreedAdminFilter.class);
    }

    @Override public void init(FilterConfig config) throws ServletException {
    }

    @Override public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        RequestInterrogator r = new RequestInterrogator(request, response);


        if (r.isDotCacheFile()) {
            CacheUtil.addCacheForeverResponseHeaders(response);
        } else if (r.isDotNocacheFile()) {
            CacheUtil.addCacheNeverResponseHeaders(response);
        } else {
            String uri = request.getRequestURI();
            if (request.getMethod().equalsIgnoreCase("GET")) {
                if (uri.contains("gwt/chrome")) {
                    CacheUtil.addCacheForXDaysResponseHeaders(response, 30);
                } else if (uri.contains("help_16.png")) {
                    CacheUtil.addCacheForeverResponseHeaders(response);
                }
            }

        }


        chain.doFilter(request, response);
    }

    @Override public void destroy() {

    }

}