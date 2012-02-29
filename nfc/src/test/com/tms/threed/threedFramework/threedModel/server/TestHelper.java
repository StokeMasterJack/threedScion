package com.tms.threed.threedFramework.threedModel.server;

import com.tms.threed.threedFramework.repo.server.Repos;

public class TestHelper {
    public static Repos getRepos() {
        return new Repos(ThreedConfig.getRepoBaseDir());
    }
}
