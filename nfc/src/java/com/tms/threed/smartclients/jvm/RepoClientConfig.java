package com.tms.threed.smartClients.jvm;

import smartsoft.util.config.ConfigHelper;
import smartsoft.util.lang.shared.Path;

import static smartsoft.util.lang.shared.Strings.isEmpty;

public class RepoClientConfig extends ConfigHelper {

    public final Path getRepoBaseUrlWww() {
        String repoBaseUrl = getProperty("repoBaseUrlWww");
        return new Path(repoBaseUrl);
    }

    public final Path getRepoBaseUrlReverseProxy() {
        String repoBaseUrlReverseProxy = getProperty("repoBaseUrlReverseProxy");
        if (isEmpty(repoBaseUrlReverseProxy)) {
            throw new IllegalStateException("repoBaseUrlReverseProxy should be non-null");
        } else {
            return new Path(repoBaseUrlReverseProxy);
        }
    }

    private static final RepoClientConfig INSTANCE = new RepoClientConfig();

    public static RepoClientConfig get() {
        return INSTANCE;
    }

}
