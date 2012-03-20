package com.tms.threed.threedCore.imageModel.shared.slice;

public interface LayerOrFeature extends Child, Parent {

    FeatureOrPng[] getChildNodes();

    Layer getLayer();

    int getDepth();
}