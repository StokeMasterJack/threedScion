package c3i.imgGen.repoImpl;

import c3i.featureModel.shared.FeatureModel;
import c3i.featureModel.shared.common.RootTreeId;
import c3i.featureModel.shared.common.SeriesId;
import c3i.imgGen.api.FeatureModelFactory;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.SeriesRepo;

import static com.google.common.base.Preconditions.checkNotNull;

public class FeatureModelFactoryRepo implements FeatureModelFactory<SeriesId> {

    private final BrandRepos brandRepos;

    public FeatureModelFactoryRepo(BrandRepos brandRepos) {
        checkNotNull(brandRepos);
        this.brandRepos = brandRepos;
    }

    @Override
    public FeatureModel createFeatureModel(SeriesId seriesId) {
        checkNotNull(seriesId);
        checkNotNull(brandRepos);

        SeriesRepo seriesRepo = brandRepos.getSeriesRepo(seriesId);
        checkNotNull(seriesRepo);

        RootTreeId rootTreeId = seriesId.getRootTreeId();
        checkNotNull(rootTreeId);

        FeatureModel featureModel = seriesRepo.createFeatureModel(rootTreeId);
        checkNotNull(featureModel);

        return featureModel;

    }
}
