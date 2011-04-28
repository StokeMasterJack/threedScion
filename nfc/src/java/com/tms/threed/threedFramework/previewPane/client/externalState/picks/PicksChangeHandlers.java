package com.tms.threed.threedFramework.previewPane.client.externalState.picks;

import com.google.gwt.event.shared.HandlerRegistration;
import com.tms.threed.threedFramework.featureModel.shared.FeatureModel;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.AssignmentException;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedFramework.featureModel.shared.picks.Picks;
import com.tms.threed.threedFramework.featureModel.shared.picks.PicksChangeEvent;
import com.tms.threed.threedFramework.featureModel.shared.picks.PicksChangeHandler;
import com.tms.threed.threedFramework.featureModel.shared.picks.PicksContextFm;
import com.tms.threed.threedFramework.featureModel.shared.picks.PicksSnapshot;
import com.tms.threed.threedFramework.featureModel.shared.picks.PicksSnapshotImpl;
import com.tms.threed.threedFramework.threedModel.client.RawPicksSnapshot;
import com.tms.threed.threedFramework.threedModel.client.VarPicksSnapshot;
import com.tms.threed.threedFramework.util.gwtUtil.client.Console;
import com.tms.threed.threedFramework.util.gwtUtil.client.MvcModel;
import com.tms.threed.threedFramework.util.lang.shared.Objects;

import java.util.Set;

public class PicksChangeHandlers extends MvcModel {

    private final FeatureModel featureModel;

    public RawPicksSnapshot currentRawPicks;
    public VarPicksSnapshot currentVarPicks;
    public PicksSnapshot currentFixedPicks;

    public PicksChangeHandlers(FeatureModel featureModel) {
        this.featureModel = featureModel;
    }

    public void fire(RawPicksSnapshot newRawPicks) {
        assert newRawPicks != null;
        if (!rawPicksChanged(newRawPicks)) return;


        this.currentRawPicks = newRawPicks;
        VarPicksSnapshot newVarPicks = null;
        try {
            newVarPicks = VarPicksSnapshot.createVarPicksSnapshot(newRawPicks, featureModel);
        } catch (VarPicksSnapshot.UnknownVarCodeFromLeftSideException e) {
            Console.error("\t" + e);
            return;
        }
        if (!varPicksChanged(newVarPicks)) return;

        VarPicksSnapshot oldVarPicks = currentVarPicks;
        currentVarPicks = newVarPicks;

        Set<Var> currentTrueUiVars = currentVarPicks.toVarSet();

        PicksSnapshot oldFixedPicks = currentFixedPicks;
        Picks newPicks = createPicks(newVarPicks);

        try {
            newPicks.fixup();
            Console.log("\tPicks are valid");
        } catch (AssignmentException e) {
            Console.log("\t\t " + e);
            return;
        }

        currentFixedPicks = newPicks.createSnapshot();
        firePicksChangeEvent(newVarPicks, oldVarPicks, oldFixedPicks, currentFixedPicks);
    }

    private Picks createPicks(VarPicksSnapshot varSnap) {
        Picks picks = new PicksSnapshotImpl(new PicksContextFm(featureModel));
        picks.pick(varSnap.modelCode);
        picks.pick(varSnap.exteriorColor);
        picks.pick(varSnap.interiorColor);
        for (Var code : varSnap.packageVars) {
            picks.pick(code);
        }
        for (Var code : varSnap.accessoryVars) {
            picks.pick(code);
        }
        return picks;
    }





    private boolean varPicksChanged(VarPicksSnapshot newVarPicks) {
        return Objects.ne(currentVarPicks, newVarPicks);
    }

    private boolean rawPicksChanged(RawPicksSnapshot newRawPicks) {
        return Objects.ne(currentRawPicks, newRawPicks);
    }

    private void firePicksChangeEvent(
            VarPicksSnapshot newVarPicks,
            VarPicksSnapshot oldVarPicks,
            PicksSnapshot oldFixedPicks,
            PicksSnapshot newFixedPicks) {

        RawPicksChangeEvent rawPicksEvent = new RawPicksChangeEvent(oldVarPicks, newVarPicks, oldFixedPicks, newFixedPicks);

        Set<Var> currentTrueUiVars = newVarPicks.toVarSet();

        Var blinkAccessory = rawPicksEvent.getBlinkAccessory();
        PicksChangeEvent picksChangeEvent = new PicksChangeEvent(currentTrueUiVars, blinkAccessory);
        try {
            fireEvent(picksChangeEvent);
        } catch (Exception e) {
            Console.log("Unexpected exception dispatching PicksChangeEvent[" + e + "]");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public HandlerRegistration addPicksChangeHandler(PicksChangeHandler h) {
        return addHandler(PicksChangeEvent.TYPE, h);
    }

}

