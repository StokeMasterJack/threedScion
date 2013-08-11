package c3i.core.imageModel.server;

import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.core.imageModel.shared.ImView;
import c3i.core.imageModel.shared.ViewLiftSpec;
import c3i.core.imageModel.shared.SrcPng;

import java.util.Set;

public class LiftedPngGenerator {

    private final ImView view;
    private final ViewLiftSpec liftSpec;
    private final Var triggerVar;

    public LiftedPngGenerator(ImView view) {
        this.view = view;
        this.liftSpec = view.getLiftSpec();
        this.triggerVar = liftSpec.getTriggerFeature();
    }

    public void generateLiftedPngs() {
        if (liftSpec == null) return;

        Set<SrcPng> pngs = view.getAllSourcePngs();

        for (SrcPng srcPng : pngs) {
            Set<Var> requiredFeatures = srcPng.getFeatures();

        }
    }
}
