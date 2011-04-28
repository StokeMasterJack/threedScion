package com.tms.threed.threedFramework.imageModel.shared;

import java.util.List;

public interface ImLayerOrFeature extends ImNode{

    List<ImFeatureOrPng> getChildNodes();
}