package c3i.core.imageModel.shared;

public class LayerSlice2 implements LayerSlice {

    private final ImLayer layer;
    private final int angle;

    public LayerSlice2(ImLayer layer, int angle) {
        this.layer = layer;
        this.angle = angle;
    }

    public ImLayer getLayer() {
        return layer;
    }

    public int getAngle() {
        return angle;
    }
}
