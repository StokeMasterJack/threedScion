package c3i.core.imageModel.shared;

import c3i.core.featureModel.shared.boolExpr.Var;


public class ViewLiftSpec {

    private Var triggerFeature;
    private int deltaY;

    public ViewLiftSpec(Var triggerFeature, int deltaY) {
        this.triggerFeature = triggerFeature;
        this.deltaY = deltaY;
    }

    public Var getTriggerFeature() {
        return triggerFeature;
    }

    public int getDeltaY() {
        return deltaY;
    }



}
