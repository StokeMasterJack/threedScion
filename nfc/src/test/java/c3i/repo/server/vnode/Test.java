package c3i.repo.server.vnode;

import c3i.repo.server.Repos;
import c3i.repo.server.SeriesRepo;

import c3i.core.common.shared.BrandKey;
import c3i.core.threedModel.shared.ThreedModel;
import junit.framework.TestCase;

public class Test extends TestCase {

    SeriesRepo seriesRepo;

    @Override protected void setUp() throws Exception {
        Repos repos = Repos.getToyotaRepoTest();
        seriesRepo = repos.getSeriesRepo(BrandKey.TOYOTA, "tundra", 2011);
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
