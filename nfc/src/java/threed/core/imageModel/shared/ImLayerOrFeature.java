package threed.core.imageModel.shared;

import java.util.List;

public interface ImLayerOrFeature extends ImNode{

    List<ImFeatureOrPng> getChildNodes();
}