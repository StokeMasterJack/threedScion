package c3i.repo.server;

import c3i.featureModel.shared.common.BrandKey;
import c3i.featureModel.shared.common.SeriesId;
import c3i.featureModel.shared.common.SeriesKey;
import com.google.common.collect.ImmutableMap;

import java.io.File;
import java.util.Map;
import java.util.logging.Logger;

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

    public SeriesRepo getSeriesRepo(SeriesId seriesId) {
        SeriesKey seriesKey = seriesId.getSeriesKey();
        return getSeriesRepo(seriesKey);
    }

    public SeriesRepo getSeriesRepo(SeriesKey seriesKey) {
        return getRepos(seriesKey.getBrandKey()).getSeriesRepo(seriesKey);
    }

    public Repos getRepos(BrandKey brandKey) {
        return reposMap.get(brandKey);
    }

    public static BrandRepos createSingleBrand(BrandKey brandKey, File repoBaseDir) {
        ImmutableMap<BrandKey, File> repoBaseDirs = ImmutableMap.of(brandKey, repoBaseDir);
        return new BrandRepos(repoBaseDirs);
    }


    private static Logger log = Logger.getLogger("c3i");

}
