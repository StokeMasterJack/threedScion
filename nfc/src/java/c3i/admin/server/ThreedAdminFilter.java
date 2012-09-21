package c3i.admin.server;

import c3i.repoWebService.ThreedRepoApp;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import smartsoft.util.servlet.http.RequestInterrogator;
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

public class ThreedAdminFilter implements Filter {

    private static final ThreedRepoApp app;
    private static final Log log;

    static {
        app = ThreedRepoApp.get();
        log = LogFactory.getLog(ThreedAdminFilter.class);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (CacheUtil.isDotCacheFile(req)) {
            CacheUtil.addCacheForeverResponseHeaders(response);
        } else if (CacheUtil.isDotNocacheFile(req)) {
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

    @Override
    public void destroy() {

    }

}