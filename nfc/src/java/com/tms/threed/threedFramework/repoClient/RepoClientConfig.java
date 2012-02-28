package com.tms.threed.threedFramework.repoClient;

import com.tms.threed.threedFramework.util.config.ConfigHelper;
import com.tms.threed.threedFramework.util.lang.shared.Path;

import static com.tms.threed.threedFramework.util.lang.shared.Strings.isEmpty;

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
