package c3i.core.imageModel.shared;

public class FeatureOrPngSlice2 implements FeatureOrPngSlice {

    private final ImFeatureOrPng featureOrPng;
    private final int angle;

    public FeatureOrPngSlice2(ImFeatureOrPng featureOrPng, int angle) {
        this.featureOrPng = featureOrPng;
        this.angle = angle;
    }

    public ImFeatureOrPng getFeatureOrPng() {
        return featureOrPng;
    }

    public int getAngle() {
        return angle;
    }
}
