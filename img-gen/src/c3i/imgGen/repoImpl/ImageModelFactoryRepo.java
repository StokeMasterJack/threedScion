package c3i.imgGen.repoImpl;

import c3i.core.common.shared.SeriesId;
import c3i.featureModel.shared.FeatureModel;
import c3i.imageModel.shared.ImageModel;
import c3i.imgGen.api.ImageModelFactory;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.SeriesRepo;

public class ImageModelFactoryRepo implements ImageModelFactory<SeriesId> {

    private final BrandRepos brandRepos;

    public ImageModelFactoryRepo(BrandRepos brandRepos) {
        this.brandRepos = brandRepos;
    }

    @Override
    public ImageModel createImageModel(FeatureModel featureModel, SeriesId seriesId) {
        SeriesRepo seriesRepo = brandRepos.getSeriesRepo(seriesId);
        return seriesRepo.createImageModel(featureModel, seriesId.getRootTreeId());
    }


}
