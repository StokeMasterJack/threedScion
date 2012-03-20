package com.tms.threed.threedCore.imageModel.shared;

import java.util.List;

public interface ImLayerOrFeature extends ImNode{

    List<ImFeatureOrPng> getChildNodes();
}