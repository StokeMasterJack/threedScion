package c3i.repo.server.vnode;

import c3i.threedModel.client.ThreedModel;
import c3i.featureModel.shared.common.BrandKey;
import c3i.repo.server.BrandRepo;
import c3i.repo.server.SeriesRepo;
import junit.framework.TestCase;

public class Test extends TestCase {

    BrandRepo brandRepo;
    SeriesRepo seriesRepo;

    @Override
    protected void setUp() throws Exception {
        brandRepo = BrandRepo.testRepoToyota();
        seriesRepo = brandRepo.getSeriesRepo(BrandKey.TOYOTA, "tundra", 2011);
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
