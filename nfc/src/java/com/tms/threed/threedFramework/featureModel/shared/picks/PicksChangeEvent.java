package com.tms.threed.threedFramework.featureModel.shared.picks;

import com.google.gwt.event.shared.GwtEvent;
import com.tms.threed.threedFramework.featureModel.shared.FeatureModel;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;

import java.util.Set;

import static com.tms.threed.threedFramework.util.lang.shared.Strings.getSimpleName;

public class PicksChangeEvent extends GwtEvent<PicksChangeHandler> {

    private final Set<Var> currentTrueUiVars;
    private final Var mostRecentSinglePick;

    public PicksChangeEvent(Set<Var> currentTrueUiVars, Var mostRecentSinglePick) {
        this.currentTrueUiVars = currentTrueUiVars;
        this.mostRecentSinglePick = mostRecentSinglePick;
    }

    /**
     * is this redundant???
     * @param fm
     * @return
     */
    public boolean isBlinkEvent(FeatureModel fm) {
        if (mostRecentSinglePick == null) return false;
        return mostRecentSinglePick.isAccessory(fm);
    }

    /**
     * is this redundant???
     * @param fm
     * @return
     */
    public Var getBlinkAccessory(FeatureModel fm) {
        if (mostRecentSinglePick == null) return null;
        if (mostRecentSinglePick.isAccessory(fm)) return mostRecentSinglePick;
        return null;
    }

    public Var getBlinkAccessory(){
        return mostRecentSinglePick;
    }

    public static final Type<PicksChangeHandler> TYPE = new Type<PicksChangeHandler>();

    @Override public Type<PicksChangeHandler> getAssociatedType() {
        return TYPE;
    }

    @Override protected void dispatch(PicksChangeHandler handler) {
        handler.onPicksChange(this);
    }

    public Set<Var> getCurrentTrueUiVars() {
        return currentTrueUiVars;
    }


    @Override public String toString() {
        return getSimpleName(this) + " [" + currentTrueUiVars + "]";
    }

//    public boolean isDirty(Var var) {
//        if (var.isLeaf()) {
//            Tri oldValue = oldPicks.getValue(var);
//            Tri newValue = newPicks.getValue(var);
//            return !oldValue.equals(newValue);
//        } else {
//            List<Var> childVars = var.getChildVars();
//            for (Var childVar : childVars) {
//                boolean childDirty = isDirty(childVar);
//                if (childDirty) return true;
//            }
//            return false;
//        }
//
//    }


}