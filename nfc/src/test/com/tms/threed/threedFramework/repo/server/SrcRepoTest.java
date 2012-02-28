package com.tms.threed.threedFramework.repo.server;

import com.tms.threed.threedFramework.repo.shared.CommitHistory;
import com.tms.threed.threedFramework.threedModel.server.ThreedConfig;
import com.tms.threed.threedFramework.threedModel.shared.SeriesKey;
import junit.framework.TestCase;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevFlag;


public class SrcRepoTest extends TestCase {

    public void test_checkin_Camry() throws Exception {
        Repos repos = ThreedConfig.getRepos();
        SeriesRepo seriesRepo = repos.getSeriesRepo(SeriesKey.CAMRY_2011);
        SrcRepo srcRepo = seriesRepo.getSrcRepo();
        srcRepo.addAllAndCommit("Commit Comment");
    }

    public void test_addAllAndCommit2() throws Exception {
        Repos repos = ThreedConfig.getRepos();
        SeriesRepo seriesRepo = repos.getSeriesRepo(SeriesKey.TUNDRA_2011);
        SrcRepo srcRepo = seriesRepo.getSrcRepo();
        srcRepo.addAllAndCommit("Poop");
    }

    public void test_getCommitHistory() throws Exception {
        Repos repos = ThreedConfig.getRepos();

        SeriesRepo seriesRepo = repos.getSeriesRepo(SeriesKey.AVALON_2011);
        SrcRepo srcRepo = seriesRepo.getSrcRepo();

        final CommitHistory head = srcRepo.getHeadCommitHistory();

        head.print();


    }


    public void test_getRevCommitEager() throws Exception {
        Repos repos = ThreedConfig.getRepos();

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
