package c3i.core.featureModel.shared.picks;

import c3i.core.featureModel.shared.boolExpr.Var;
import com.google.common.collect.ImmutableSet;
import com.google.gwt.event.shared.GwtEvent;

import static smartsoft.util.shared.Strings.getSimpleName;

public class PicksChangeEvent extends GwtEvent<PicksChangeHandler> {

    private final ImmutableSet<Var> currentTrueUiVars;
    private final Var mostRecentSinglePick;

    public PicksChangeEvent(ImmutableSet<Var> currentTrueUiVars, Var mostRecentSinglePick) {
        this.currentTrueUiVars = currentTrueUiVars;
        this.mostRecentSinglePick = mostRecentSinglePick;
    }

    public static final Type<PicksChangeHandler> TYPE = new Type<PicksChangeHandler>();

    @Override
    public Type<PicksChangeHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(PicksChangeHandler handler) {
        handler.onPicksChange(this);
    }

    public ImmutableSet<Var> getCurrentTrueUiVars() {
        return currentTrueUiVars;
    }


    @Override
    public String toString() {
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