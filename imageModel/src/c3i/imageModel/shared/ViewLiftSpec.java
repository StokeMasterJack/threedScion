package c3i.imageModel.shared;

public class ViewLiftSpec {

    private Object triggerFeature;
    private int deltaY;

    public ViewLiftSpec(Object triggerFeature, int deltaY) {
        this.triggerFeature = triggerFeature;
        this.deltaY = deltaY;
    }

    public Object getTriggerFeature() {
        return triggerFeature;
    }

    public int getDeltaY() {
        return deltaY;
    }


}
