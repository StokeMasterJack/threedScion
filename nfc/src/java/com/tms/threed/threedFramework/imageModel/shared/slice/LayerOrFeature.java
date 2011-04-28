package com.tms.threed.threedFramework.imageModel.shared.slice;

public interface LayerOrFeature extends Child, Parent {

    FeatureOrPng[] getChildNodes();

    Layer getLayer();

    int getDepth();
}