package c3i.repo.server;

import c3i.core.common.shared.BrandKey;
import c3i.core.imageModel.shared.Profile;
import c3i.jpgGen.server.taskManager.JpgGeneratorService;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.Map;

public class BrandRepos {

    private ImmutableMap<BrandKey, Repos> reposMap;
    private ImmutableMap<BrandKey, JpgGeneratorService> jpgGenMap;

    public BrandRepos(ImmutableMap<BrandKey, File> repoBaseDirs) {
        System.out.println("repoBaseDirs = " + repoBaseDirs);
        log.info("repoBaseDirs = " + repoBaseDirs);
        ImmutableMap.Builder<BrandKey, Repos> b1 = ImmutableMap.builder();
        ImmutableMap.Builder<BrandKey, JpgGeneratorService> b2 = ImmutableMap.builder();
        for (Map.Entry<BrandKey, File> entry : repoBaseDirs.entrySet()) {
            BrandKey brandKey = entry.getKey();
            File repoBaseDir = entry.getValue();
            Repos repos = new Repos(brandKey, repoBaseDir);
            b1.put(brandKey, repos);
            b2.put(brandKey, new JpgGeneratorService(repos));
        }
        reposMap = b1.build();
        jpgGenMap = b2.build();
    }

    public Repos getRepos(BrandKey brandKey) {
        return reposMap.get(brandKey);
    }

    public JpgGeneratorService getJpgGeneratorService(BrandKey brandKey) {
        return jpgGenMap.get(brandKey);
    }

    public void shutdown() {
        for (JpgGeneratorService jpgGen : jpgGenMap.values()) {
            log.info("\t Shutting down JpgGenerator..");

            jpgGen.stopAndWait();
            log.info("\tJpgGenerator shutdown complete");
        }


    }

    private static Log log = LogFactory.getLog(BrandRepos.class);

}
