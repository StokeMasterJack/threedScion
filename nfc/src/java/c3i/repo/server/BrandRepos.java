package c3i.repo.server;

import c3i.core.common.shared.BrandKey;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.Map;

public class BrandRepos {

    private ImmutableMap<BrandKey, Repos> reposMap;

    public BrandRepos(ImmutableMap<BrandKey, File> repoBaseDirs) {
        log.info("repoBaseDirs = " + repoBaseDirs);
        ImmutableMap.Builder<BrandKey, Repos> b1 = ImmutableMap.builder();
        for (Map.Entry<BrandKey, File> entry : repoBaseDirs.entrySet()) {
            BrandKey brandKey = entry.getKey();
            File repoBaseDir = entry.getValue();
            Repos repos = new Repos(brandKey, repoBaseDir);
            b1.put(brandKey, repos);
        }
        reposMap = b1.build();
    }

    public Repos getRepos(BrandKey brandKey) {
        return reposMap.get(brandKey);
    }

    public static BrandRepos createSingleBrand(BrandKey brandKey, File repoBaseDir) {
        ImmutableMap<BrandKey, File> repoBaseDirs = ImmutableMap.of(brandKey, repoBaseDir);
        return new BrandRepos(repoBaseDirs);
    }


    private static Log log = LogFactory.getLog(BrandRepos.class);

}
