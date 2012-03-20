package com.tms.threedToyota.byt.client.externalState.picks;

import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedCore.featureModel.shared.picks.PicksSnapshot;
import com.tms.threed.threedCore.threedModel.client.VarPicksSnapshot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

import static smartsoft.util.lang.shared.Strings.getSimpleName;

public class RawPicksChangeEvent {

    @Nullable private final VarPicksSnapshot oldPicks;
    @Nonnull private final VarPicksSnapshot newPicks;

    @Nonnull private final PicksSnapshot oldFixedPicks;
    @Nonnull private final PicksSnapshot newFixedPicks;

    private final boolean modelChanged;
    private final boolean exteriorColorChanged;
    private final boolean interiorColorChanged;
    private final boolean packageCodesChanged;
    private final boolean accessoryCodesChanged;

    private final boolean initialChange;
    private final boolean coreChange;
    private final Var blinkAccessory;

    public RawPicksChangeEvent(
            VarPicksSnapshot oldPicks,
            VarPicksSnapshot newPicks, PicksSnapshot oldFixedPicks, PicksSnapshot newFixedPicks) {

        this.oldPicks = oldPicks;
        this.newPicks = newPicks;

        this.oldFixedPicks = oldFixedPicks;
        this.newFixedPicks = newFixedPicks;

        initialChange = oldPicks == null;

        if (initialChange) {
            modelChanged = true;
            exteriorColorChanged = true;
            interiorColorChanged = true;
            packageCodesChanged = newPicks.packageCount > 0;
            accessoryCodesChanged = newPicks.accessoryCount > 0;
        } else {
            modelChanged = !oldPicks.modelCode.equals(newPicks.modelCode);
            exteriorColorChanged = !oldPicks.exteriorColor.equals(newPicks.exteriorColor);
            interiorColorChanged = !oldPicks.interiorColor.equals(newPicks.interiorColor);
            packageCodesChanged = !oldPicks.packageVars.equals(newPicks.packageVars);
            accessoryCodesChanged = !oldPicks.accessoryVars.equals(newPicks.accessoryVars);
        }

        coreChange = initialChange || modelChanged || exteriorColorChanged || interiorColorChanged;

        blinkAccessory = initBlinkAccessory();

    }

    private Var initBlinkAccessory() {
        if (initialChange) return null;
        if (!accessoryCodesChanged) return null;
        Set<Var> beforeSet = oldPicks.accessoryVars;
        Set<Var> afterSet = newPicks.accessoryVars;

        for (Var var : afterSet) {
            boolean isInsert = !beforeSet.contains(var);
            if (isInsert) return var;
        }
        return null;
    }

    public Var getBlinkAccessory() {
        return blinkAccessory;
    }

    public boolean isBlinkEvent() {
        return blinkAccessory != null;
    }

    public VarPicksSnapshot getOldPicks() {
        return oldPicks;
    }

    public VarPicksSnapshot getNewPicks() {
        return newPicks;
    }

    @Override public String toString() {
        return getSimpleName(this) + " [" + newPicks + "]";
    }

    public PicksSnapshot getNewFixedPicks() {
        return newFixedPicks;
    }

    public PicksSnapshot getOldFixedPicks() {
        return oldFixedPicks;
    }
}