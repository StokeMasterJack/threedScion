package com.tms.threed.threedFramework.imageModel.shared.slice;

/**
 * Immutible
 */
public interface Node {

    //returns null for root node
    String getSimpleName();

    boolean isImageModel();

    boolean isLayer();

    boolean isFeature();

    boolean isPng();

    ImageSlice asImageModel();

    Layer asLayer();

    Feature asFeature();

    Png asPng();

    Parent getParent();

    int getDepth();


}
