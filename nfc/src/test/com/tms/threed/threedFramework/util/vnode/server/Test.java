package com.tms.threed.threedFramework.util.vnode.server;

import com.tms.threed.threedFramework.repo.server.Repos;
import com.tms.threed.threedFramework.repo.server.SeriesRepo;
import com.tms.threed.threedFramework.threedCore.server.config.ThreedConfig;
import com.tms.threed.threedFramework.threedModel.shared.ThreedModel;
import junit.framework.TestCase;

public class Test extends TestCase {

    Repos repos;
    SeriesRepo seriesRepo;

    @Override protected void setUp() throws Exception {
        repos = ThreedConfig.getRepos();
        seriesRepo = repos.getSeriesRepo("tundra", 2011);
    }


    public void testGit() throws Exception {
        long t1 = System.currentTimeMillis();
        ThreedModel threedModel = seriesRepo.getThreedModelHead();
        long t2 = System.currentTimeMillis();
        System.out.println("git Delta: " + (t2 - t1));
    }


    public void testNoGit() throws Exception {
        long t1 = System.currentTimeMillis();
        ThreedModel threedModel = seriesRepo.createThreedModelFromWork();
        long t2 = System.currentTimeMillis();
        System.out.println("No git Delta: " + (t2 - t1));
    }


}
