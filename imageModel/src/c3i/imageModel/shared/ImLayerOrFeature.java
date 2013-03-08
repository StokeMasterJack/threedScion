package c3i.imageModel.shared;

import java.util.List;

public interface ImLayerOrFeature extends ImNode {

    List<ImFeatureOrPng> getChildNodes();
}