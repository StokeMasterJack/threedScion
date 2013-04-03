package c3i.imageModel.shared;

public class ViewLiftSpec {

    private String triggerFeature;
    private int deltaY;

    public ViewLiftSpec(String triggerFeature, int deltaY) {
        this.triggerFeature = triggerFeature;
        this.deltaY = deltaY;
    }

    public String getTriggerFeature() {
        return triggerFeature;
    }

    public int getDeltaY() {
        return deltaY;
    }


}
