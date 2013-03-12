package c3i.imageModel.shared;

import java.util.List;

public interface ImLayerOrFeature<V> extends ImNode<V> {

    List<ImFeatureOrPng<V>> getChildNodes();
}