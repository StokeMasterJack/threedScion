package c3i.imgGen.repoImpl;

import c3i.core.common.shared.SeriesId;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.imgGen.api.FeatureModelFactory;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.SeriesRepo;

public class FeatureModelFactoryRepo implements FeatureModelFactory<SeriesId> {

    private final BrandRepos brandRepos;

    public FeatureModelFactoryRepo(BrandRepos brandRepos) {
        this.brandRepos = brandRepos;
    }

    @Override
    public FeatureModel createFeatureModel(SeriesId seriesId) {
        SeriesRepo seriesRepo = brandRepos.getSeriesRepo(seriesId);
        return seriesRepo.createFeatureModel(seriesId.getRootTreeId());

    }
}
