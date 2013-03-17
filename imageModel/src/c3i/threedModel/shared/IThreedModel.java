package c3i.threedModel.shared;

import c3i.featureModel.shared.IFeatureModel;
import c3i.imageModel.shared.IImageModel;

public interface IThreedModel<K, V> {

    IFeatureModel<K, V> getFeatureModel();

    IImageModel<K, V> getImageModel();

}
