package c3i.repo.server;

import c3i.core.imageModel.server.ImageUtil;

import c3i.core.common.shared.BrandKey;
import c3i.core.threedModel.shared.ThreedModel;
import junit.framework.TestCase;
import org.eclipse.jgit.revwalk.RevCommit;


public class ReposTest extends TestCase {

    Repos repos = Repos.get();

    public void test0() throws Exception {
        final SeriesRepo seriesRepo = repos.getSeriesRepo(BrandKey.TOYOTA, "avalon", 2011);
        final SrcRepo srcRepo = seriesRepo.getSrcRepo();
        final RevCommit revCommit = srcRepo.addAllAndCommit("init");



        ThreedModel threedModel = repos.getThreedModel(BrandKey.TOYOTA, "avalon", 2011);
        threedModel.print();
    }

    public void test1() throws Exception {
        ThreedModel threedModel = repos.getThreedModel(BrandKey.TOYOTA, "avalon", 2011);
        threedModel.print();
    }

    public void test_getThreedModel() throws Exception {
        ThreedModel threedModel = repos.getThreedModel(BrandKey.TOYOTA, "avalon", 2011);
        threedModel.print();
    }

    public void testGetShortId() throws Exception {
        final String jpgLongId = "1cd92-3e498";
        String jpgShortId = ImageUtil.getFingerprint(jpgLongId);
        assertEquals("b800ca04c2d100d6b2727e8549ce4179504f7aca", jpgShortId);
    }


}
