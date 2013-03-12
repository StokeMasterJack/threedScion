package c3i.imageModel.shared;

public class ViewLiftSpec<V> {

    private V triggerFeature;
    private int deltaY;

    public ViewLiftSpec(V triggerFeature, int deltaY) {
        this.triggerFeature = triggerFeature;
        this.deltaY = deltaY;
    }

    public V getTriggerFeature() {
        return triggerFeature;
    }

    public int getDeltaY() {
        return deltaY;
    }


}
