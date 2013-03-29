package c3i.repo.server;

import c3i.threedModel.client.ThreedModel;
import c3i.featureModel.shared.common.BrandKey;
import c3i.imageModel.server.ImageUtil;
import junit.framework.TestCase;
import org.eclipse.jgit.revwalk.RevCommit;


public class BrandRepoTest extends TestCase {

    BrandRepo brandRepo;

    @Override
    protected void setUp() throws Exception {
        brandRepo = BrandRepo.testRepoToyota();
    }

    public void test0() throws Exception {
        final SeriesRepo seriesRepo = brandRepo.getSeriesRepo(BrandKey.TOYOTA, "avalon", 2011);
        final SrcRepo srcRepo = seriesRepo.getSrcRepo();
        final RevCommit revCommit = srcRepo.addAllAndCommit("init");


        ThreedModel threedModel = brandRepo.getThreedModel(BrandKey.TOYOTA, "avalon", 2011);
        threedModel.print();
    }

    public void test1() throws Exception {
        ThreedModel threedModel = brandRepo.getThreedModel(BrandKey.TOYOTA, "avalon", 2011);
        threedModel.print();
    }

    public void test_getThreedModel() throws Exception {
        ThreedModel threedModel = brandRepo.getThreedModel(BrandKey.TOYOTA, "avalon", 2011);
        threedModel.print();
    }

    public void testGetShortId() throws Exception {
        final String jpgLongId = "1cd92-3e498";
        String jpgShortId = ImageUtil.getFingerprint(jpgLongId);
        assertEquals("b800ca04c2d100d6b2727e8549ce4179504f7aca", jpgShortId);
    }


}
