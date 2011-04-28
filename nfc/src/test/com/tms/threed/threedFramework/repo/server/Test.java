package com.tms.threed.threedFramework.repo.server;

import com.tms.threed.threedFramework.repo.shared.TagCommit;
import com.tms.threed.threedFramework.threedCore.config.ThreedConfig;
import com.tms.threed.threedFramework.threedCore.shared.SeriesKey;
import junit.framework.TestCase;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepository;

import java.util.List;

public class Test extends TestCase {

    public void test1() throws Exception {
        Repos repos = ThreedConfig.getRepos();

        SeriesRepo seriesRepo = repos.getSeriesRepo(SeriesKey.AVALON_2011);
        SrcRepo srcRepo = seriesRepo.getSrcRepo();

        List<TagCommit> tagCommits = srcRepo.getTagCommits();

        System.out.println();
        System.out.println();
        System.out.println();

        for (TagCommit tagCommit : tagCommits) {
            System.out.println("tagCommit = " + tagCommit);
        }

    }


    public void test2() throws Exception {
        Repos repos = ThreedConfig.getRepos();

        SeriesRepo seriesRepo = repos.getSeriesRepo(SeriesKey.AVALON_2011);
        SrcRepo srcRepo = seriesRepo.getSrcRepo();

        FileRepository gitRepo = srcRepo.getGitRepo();

        Git g = new Git(gitRepo);
        LogCommand log1 = g.log();
        Iterable<RevCommit> call = log1.call();

        for (RevCommit revCommit : call) {
            System.out.println(revCommit.getTree().getId().getName());
        }


    }

    public void test3() throws Exception {
        Repos repos = ThreedConfig.getRepos();

        SeriesRepo seriesRepo = repos.getSeriesRepo(SeriesKey.AVALON_2011);
        SrcRepo srcRepo = seriesRepo.getSrcRepo();

        FileRepository repo = srcRepo.getGitRepo();


        RevWalk walk = new RevWalk(repo);


        ObjectId headId = repo.resolve(Constants.HEAD);


        walk.markStart(walk.lookupCommit(headId));

        RevCommit revCommit = walk.next();
        System.out.println(revCommit.getTree().getId().getName());


//        for (RevCommit revCommit : walk) {
//             System.out.println(revCommit.getTree().getId().getName());
//        }


    }


}
