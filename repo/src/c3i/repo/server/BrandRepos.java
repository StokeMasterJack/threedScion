package c3i.repo.server;

import c3i.featureModel.shared.common.BrandKey;
import c3i.featureModel.shared.common.SeriesId;
import c3i.featureModel.shared.common.SeriesKey;
import c3i.repo.RepoConfig;
import c3i.repo.ReposConfig;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class BrandRepos {

    private ReposConfig reposConfig;

    private final LoadingCache<BrandKey, BrandRepo> reposMap;

    public BrandRepos(final ReposConfig reposConfig) {
        this.reposConfig = reposConfig;

        reposMap = CacheBuilder.newBuilder()
                .concurrencyLevel(4)
                .maximumSize(1000)
                .build(
                        new CacheLoader<BrandKey, BrandRepo>() {
                            @Override
                            public BrandRepo load(BrandKey brandKey) throws Exception {
                                RepoConfig repoConfig = reposConfig.getRepoConfig(brandKey);
                                return new BrandRepo(repoConfig);
                            }
                        });

    }

    public BrandRepos(File... repoBaseDirs) {
        this(new ReposConfig(repoBaseDirs));
    }

    public BrandRepos(ImmutableMap<BrandKey, File> repoBaseDirs) {
        this(new ReposConfig(repoBaseDirs));
    }

    public SeriesRepo getSeriesRepo(SeriesId seriesId) {
        SeriesKey seriesKey = seriesId.getSeriesKey();
        return getSeriesRepo(seriesKey);
    }

    public SeriesRepo getSeriesRepo(SeriesKey seriesKey) {
        return getBrandRepo(seriesKey.getBrandKey()).getSeriesRepo(seriesKey);
    }


    public BrandRepo getBrandRepo(BrandKey brandKey) {
        try {
            return reposMap.get(brandKey);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static BrandRepos createSingleBrand(BrandKey brandKey, File repoBaseDir) {
        ImmutableMap<BrandKey, File> repoBaseDirs = ImmutableMap.of(brandKey, repoBaseDir);
        return new BrandRepos(repoBaseDirs);
    }

    public static BrandRepos testRepos() {
        ReposConfig cfg = ReposConfig.testRepos();
        return new BrandRepos(cfg);
    }


    private static Logger log = Logger.getLogger("c3i");

}
