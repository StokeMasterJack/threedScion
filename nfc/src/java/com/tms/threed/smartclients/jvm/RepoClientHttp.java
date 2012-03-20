package com.tms.threed.smartClients.jvm;

import smartsoft.util.lang.shared.Path;

import javax.servlet.ServletContext;

public class RepoClientHttp {

    public static RepoClient getRepoClient(ServletContext application) {
        String attrName = RepoClient.class.getName();
        RepoClient repoClient = (RepoClient) application.getAttribute(attrName);
        if (repoClient == null) {
            Path repoBaseUrlReverseProxy = RepoClientConfig.get().getRepoBaseUrlReverseProxy();

            if (repoBaseUrlReverseProxy == null) {
                throw new IllegalArgumentException("repoBaseUrlReverseProxy must be non-null");
            }

            if (!repoBaseUrlReverseProxy.isHttpUrl()) {
                throw new IllegalArgumentException("Invalid repoUrl[" + repoBaseUrlReverseProxy + "]");
            }


            repoClient = new RepoClient(repoBaseUrlReverseProxy);
            application.setAttribute(attrName, repoClient);
        }
        return repoClient;
    }


}
