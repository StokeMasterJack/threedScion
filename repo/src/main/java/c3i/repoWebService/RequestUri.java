package c3i.repoWebService;

import smartsoft.util.shared.Path;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * Complete URL:
 *      http://smartsoftdev.net/configurator-content-v2/toyota/avalon/2011/3d/jpgs/wStd/cae39a7-180eda8.jpg
 *      http://[domain]/[contextPath]/[brand]/[series]/[year]/3d/jpgs/[profile]/[png-segments].[ext]
 *      http://[domain]/[contextPath]/[series-key]/3d/jpgs/[profile]/[png-segments].[ext]
 *
 * requestUri:
 *      /configurator-content-v2/toyota/avalon/2011/3d/jpgs/wStd/cae39a7-180eda8.jpg
 *      /[contextPath]/[brand]/[series]/[year]/3d/jpgs/[profile]/[png-segments].[ext]
 */
public class RequestUri {

    protected final Path requestUri;
    protected final Path contextPath;
    protected final Path contextRelativePath;

    public RequestUri(Path requestUri, Path contextPath) {
        this.requestUri = requestUri;
        this.contextPath = contextPath;
        this.contextRelativePath = requestUri.leftTrim(contextPath);
    }

    public RequestUri(String requestUri, String contextPath) {
        this(new Path(requestUri), new Path(contextPath));
    }

    public static RequestUri parse(HttpServletRequest request) {
        return new RequestUri(request.getRequestURI(), request.getContextPath());
    }

    public Path getRequestUri() {
        return requestUri;
    }

    public Path getContextPath() {
        return contextPath;
    }
}
