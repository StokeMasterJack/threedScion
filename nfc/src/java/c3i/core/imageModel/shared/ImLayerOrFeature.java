package c3i.core.imageModel.shared;

import java.util.List;

public interface ImLayerOrFeature extends ImNode{

    List<ImFeatureOrPng> getChildNodes();
}