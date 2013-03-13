package c3i.imgGen.api;

import c3i.featureModel.shared.FeatureModel;
import c3i.imageModel.shared.ImageModel;

public interface ImageModelFactory<ID> {

    ImageModel createImageModel(FeatureModel featureModel, ID id);

}
