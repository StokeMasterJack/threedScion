package com.tms.threed.threedFramework.imageModel.shared.slice;

public abstract class BaseNode implements Node {


    public int getDepth() {
        if (this instanceof Layer) {
            return -1;
        } else if (this instanceof FeatureOrPng) {
            return getParent().getDepth() + 1;
        } else {
            throw new IllegalStateException();
        }
    }


    @Override public boolean isImageModel() {
        return this instanceof ImageSlice;
    }

    @Override public boolean isLayer() {
        return this instanceof Layer;
    }

    @Override public boolean isFeature() {
        return this instanceof Feature;
    }

    @Override public boolean isPng() {
        return this instanceof Png;
    }


    @Override public ImageSlice asImageModel() {
        return (ImageSlice) this;
    }

    @Override public Layer asLayer() {
        return (Layer) this;
    }

    @Override public Feature asFeature() {
        return (Feature) this;
    }

    @Override public Png asPng() {
        return (Png) this;
    }
}
