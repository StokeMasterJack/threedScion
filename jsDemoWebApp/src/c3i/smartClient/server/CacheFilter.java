package c3i.smartClient.server;

import smartsoft.util.servlet.http.headers.CacheUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

public class CacheFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("SmartClient CacheFilter.init");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (CacheUtil.isDotCacheFile(req)) {
            CacheUtil.addCacheForeverResponseHeaders(response);
        } else {
            int maxAgeCdn = 24; //hours
            int maxAgeBrowser = 4; //hours
            CacheUtil.addCacheForXHoursResponseHeaders(response, maxAgeCdn, maxAgeBrowser);
//            CacheUtil.addCacheForXHoursResponseHeaders(response, 24);
        }

        chain.doFilter(request, response);

    }


    @Override
    public void destroy() {
        log.info("SmartClient CacheFilter.destroy");
    }

    private static Logger log = Logger.getLogger(CacheFilter.class.getName());
}
