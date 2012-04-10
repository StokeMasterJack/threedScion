package threed.repo.server.vnode;

import threed.repo.server.Repos;
import threed.repo.server.SeriesRepo;

import threed.core.threedModel.shared.BrandKey;
import threed.core.threedModel.shared.ThreedModel;
import junit.framework.TestCase;

public class Test extends TestCase {

    Repos repos;
    SeriesRepo seriesRepo;

    @Override protected void setUp() throws Exception {
        repos = Repos.get();
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
