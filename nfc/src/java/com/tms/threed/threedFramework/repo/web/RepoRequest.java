package com.tms.threed.threedFramework.repo.web;

import com.google.common.base.Strings;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class RepoRequest {

    protected final HttpServletRequest request;
    protected final HttpServletResponse response;

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


    }

    public String getExtension() {
        return extension;
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
