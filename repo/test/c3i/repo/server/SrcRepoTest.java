package c3i.repo.server;

import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesKey;
import c3i.core.threedModel.server.TestConstants;
import c3i.repo.shared.CommitHistory;
import junit.framework.TestCase;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevFlag;

import java.util.Set;


public class SrcRepoTest extends TestCase {

    Repos repos;

    @Override
    public void setUp() throws Exception {
        repos = new Repos(BrandKey.TOYOTA, TestConstants.TOYOTA_REPO_BASE_DIR);
    }

    public void test_init() throws Exception {
        Set<SeriesKey> seriesKeys = repos.getSeriesKeys();
        for (SeriesKey seriesKey : seriesKeys) {
            SeriesRepo seriesRepo = repos.getSeriesRepo(seriesKey);
            SrcRepo srcRepo = seriesRepo.getSrcRepo();
        }
    }

    public void test_checkin_Camry() throws Exception {
        SeriesRepo seriesRepo = repos.getSeriesRepo(SeriesKey.CAMRY_2011);
        SrcRepo srcRepo = seriesRepo.getSrcRepo();
        srcRepo.addAllAndCommit("Commit Comment");
    }

    public void test_addAllAndCommit2() throws Exception {
        SeriesRepo seriesRepo = repos.getSeriesRepo(SeriesKey.TUNDRA_2011);
        SrcRepo srcRepo = seriesRepo.getSrcRepo();
        srcRepo.addAllAndCommit("Poop");
    }

    public void test_getCommitHistory() throws Exception {
        SeriesRepo seriesRepo = repos.getSeriesRepo(SeriesKey.AVALON_2011);
        SrcRepo srcRepo = seriesRepo.getSrcRepo();

        final CommitHistory head = srcRepo.getHeadCommitHistory();

        head.print();


    }


    public void test_getRevCommitEager() throws Exception {
        SeriesRepo seriesRepo = repos.getSeriesRepo(SeriesKey.AVALON_2011);
        SrcRepo srcRepo = seriesRepo.getSrcRepo();

        ObjectId head = srcRepo.resolveCommitHead();

        final RevCommit revCommit = srcRepo.getRevCommitEager(head);

        print(revCommit);
    }


    public void print(RevCommit revCommit) throws Exception {
        System.out.println(revCommit.getName());
        System.out.println(revCommit.has(RevFlag.UNINTERESTING));
        System.out.println(revCommit.getCommitTime());
        System.out.println(revCommit.getRawBuffer());
//        System.out.println(revCommit.getFullMessage());
        System.out.println();

        if (revCommit.getParentCount() > 0) {
            final RevCommit parent = revCommit.getParent(0);
            print(parent);
        }

    }

//    public void print(CommitHistory revCommit, int depth) throws Exception {
//        prindent(depth, "CommitId: " + revCommit.getCommitId());
//        prindent(depth, "RootTreeId: " + revCommit.getRootTreeId() + "");
//        prindent(depth, "Tag: " + revCommit.getTag());
////        prindent(depth,"FullMessage: " + revCommit.getFullMessage());
//        prindent(depth, "ShortMessage: " + revCommit.getShortMessage());
//        prindent(depth, "Committer: " + revCommit.getCommitter());
//        prindent(depth, "CommitTime: " + revCommit.getCommitTime());
//
//        if (revCommit.getParents().length > 0) {
//            final CommitHistory parent = revCommit.getParents()[0];
//            print(parent, depth + 1);
//        }
//
//    }


}
