package threed.core.imageModel.shared.slice;

public interface LayerOrFeature extends Child, Parent {

    FeatureOrPng[] getChildNodes();

    Layer getLayer();

    int getDepth();
}