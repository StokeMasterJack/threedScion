package c3i.repoWebService;

import com.google.common.base.Strings;
import c3i.core.common.shared.BrandKey;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class RepoRequest {

    public static final int INDEX_BRAND = 0;
    public static final int INDEX_SERIES_NAME = 1;
    public static final int INDEX_SERIES_YEAR = 2;
    public static final int INDEX_ROOT_TREE_COMMIT_ID = 3;
    protected final HttpServletRequest request;
    protected final HttpServletResponse response;

    protected final BrandKey brandKey;

    protected String baseErrorMessage;
    protected String extension;
    protected String uri;

    public RepoRequest(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;

        this.uri = request.getRequestURI();

        baseErrorMessage = "Invalid Request URI [" + uri + "]. ";

        if (Strings.isNullOrEmpty(uri)) throw new NotFoundException(baseErrorMessage + "URI cannot be empty.");


        int lastDot = uri.lastIndexOf('.');
        if (lastDot == -1) {
            extension = null;
        } else {
            String ext = uri.substring(lastDot + 1);
            if (ext == null || ext.length() == 0) {
                this.extension = null;
            } else {
                this.extension = ext;
            }
        }


        String pathInfo = request.getPathInfo();

        if (Strings.isNullOrEmpty(pathInfo)) throw new NotFoundException(baseErrorMessage);

        pathInfo = pathInfo.trim();
        if (pathInfo.substring(0, 1).equals("/")) {
            pathInfo = pathInfo.substring(1);
        }


        String[] a = pathInfo.split("/");
        if (a == null || a.length < 1) throw new NotFoundException(baseErrorMessage);

        String brandKeyString = a[INDEX_BRAND];

        try {
            this.brandKey = BrandKey.fromString(brandKeyString);
        } catch (Exception e) {
            throw new NotFoundException(baseErrorMessage + e.toString(), e);
        }


    }


    public String getExtension() {
        return extension;
    }

    public BrandKey getBrandKey() {
        return brandKey;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public String getUri() {
        return uri;
    }
}
