package c3i.imgGen.repoImpl;

import c3i.featureModel.shared.FeatureModel;
import c3i.featureModel.shared.common.SeriesId;
import c3i.imageModel.shared.ImageModel;
import c3i.imgGen.api.FeatureModelFactory;
import c3i.imgGen.api.ImageModelFactory;
import c3i.imgGen.api.ThreedModelFactory;
import c3i.threedModel.client.ThreedModel;

import static com.google.common.base.Preconditions.checkNotNull;

public class ThreedModelFactoryRepo implements ThreedModelFactory<SeriesId> {

    private final FeatureModelFactory<SeriesId> fmFactory;
    private final ImageModelFactory<SeriesId> imFactory;

    public ThreedModelFactoryRepo(FeatureModelFactory<SeriesId> fmFactory, ImageModelFactory<SeriesId> imFactory) {
        checkNotNull(fmFactory);
        checkNotNull(imFactory);
        this.fmFactory = fmFactory;
        this.imFactory = imFactory;
    }

    @Override
    public ThreedModel createThreedModel(SeriesId seriesId) {
        FeatureModel featureModel = fmFactory.createFeatureModel(seriesId);
        ImageModel imageModel = imFactory.createImageModel(featureModel, seriesId);
        return new ThreedModel(featureModel, imageModel);
    }

}
