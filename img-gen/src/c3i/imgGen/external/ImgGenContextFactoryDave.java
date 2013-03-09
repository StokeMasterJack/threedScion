package c3i.imgGen.external;

import c3i.core.common.shared.SeriesId;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.SeriesRepo;

import javax.annotation.Nonnull;

public class ImgGenContextFactoryDave implements ImgGenContextFactory<SeriesId> {

    private final BrandRepos brandRepos;

    public ImgGenContextFactoryDave(BrandRepos brandRepos) {
        this.brandRepos = brandRepos;
    }

    @Nonnull
    @Override
    public ImgGenContext getImgGenContext(SeriesId key) {
        SeriesRepo seriesRepo = brandRepos.getSeriesRepo(key);
        return new ImgGenContextDave(key, seriesRepo);
    }
}
