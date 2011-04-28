package com.tms.threed.threedFramework.repo.server;

import com.tms.threed.threedFramework.imageModel.server.ImageUtil;
import com.tms.threed.threedFramework.threedCore.config.ThreedConfig;
import com.tms.threed.threedFramework.threedCore.shared.SeriesKey;
import com.tms.threed.threedFramework.threedModel.shared.ThreedModel;
import junit.framework.TestCase;
import org.eclipse.jgit.lib.Ref;

import java.io.File;
import java.util.Map;


public class RepoTest extends TestCase {

    Repos repos = ThreedConfig.getRepos();

    private String repoBaseUrl = "http://smartsoftdev.net/configurator-content";
    private final File repoBaseDir = new File("/configurator-content");

    public void test() throws Exception {
        ThreedModel threedModel = repos.getThreedModel("avalon", 2011);
        threedModel.print();
    }

    public void testGetShortId() throws Exception {
        final String jpgLongId = "1cd92-3e498";
        String jpgShortId = ImageUtil.getFingerprint(jpgLongId);
        assertEquals("b800ca04c2d100d6b2727e8549ce4179504f7aca", jpgShortId);
    }

    public void testLookupModelXml() throws Exception {
//        SeriesRepo seriesDb = new SeriesRepo(repoBase, new SeriesKey(2011, "avalon"));
//        String commitId = "d1ed8dcb174ee13018ff19ed0ced61f60666ae76";
//        String revisionParameter = commitId + ":model.xml";
//        ObjectLoader loader = seriesDb.getSrcRepo().getRepoObject(new RevisionParameter(revisionParameter));
//
//        FileOutputStream os = new FileOutputStream("/temp/m.xml");
//        loader.copyTo(os);
//
//        os.close();
    }


    public void testLog() throws Exception {
        SeriesRepo seriesDb = new SeriesRepo(repoBaseDir, new SeriesKey(2011, "avalon"));

        Map<String, Ref> tags = seriesDb.getSrcRepo().getTags();

        for (Map.Entry<String, Ref> entry : tags.entrySet()) {
            String shortTagName = entry.getKey();
            System.out.println(shortTagName);
            Ref ref = entry.getValue();
            System.out.println(ref);
            System.out.println();
        }


    }


}
