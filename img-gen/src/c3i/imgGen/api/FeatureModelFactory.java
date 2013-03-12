package c3i.imgGen.api;

import c3i.core.featureModel.shared.FeatureModel;

public interface FeatureModelFactory<ID> {

    FeatureModel createFeatureModel(ID id);

}
