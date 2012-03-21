package com.tms.threed.threedAdmin.client.featurePicker;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.gwt.event.shared.SimpleEventBus;
import com.tms.threed.threedCore.featureModel.shared.FeatureModel;
import com.tms.threed.threedCore.featureModel.shared.FixResult;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.AssignmentException;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedCore.featureModel.shared.picks.UiPicksChangeEvent;
import com.tms.threed.threedCore.featureModel.shared.picks.UiPicksChangeHandler;
import com.tms.threed.threedCore.threedModel.client.SimplePicks2;
import com.tms.threed.threedCore.threedModel.shared.ThreedModel;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CurrentUiPicks implements UiPicks, SimplePicks2 {

    private final ThreedModel threedModel;
    private final FeatureModel featureModel;
    private final Set<Var> uiVars;

    private final Set<Var> currentTrueUiVars;

    private final SimpleEventBus bus = new SimpleEventBus();

    private FixResult fixResult;

    private Var potentialBlinkVar;

    public CurrentUiPicks(ThreedModel threedModel) {
        this.threedModel = threedModel;
        this.featureModel = threedModel.getFeatureModel();
        this.uiVars = featureModel.getPickableVars();
        Set<Var> initiallyTruePickableVars = featureModel.getInitiallyTruePickableVars();
        this.currentTrueUiVars = initiallyTruePickableVars;
    }

    public CurrentUiPicks(ThreedModel threedModel, Set<Var> initiallyTruePickableVars) {
        this.threedModel = threedModel;
        this.featureModel = threedModel.getFeatureModel();
        this.uiVars = featureModel.getPickableVars();
        this.currentTrueUiVars = initiallyTruePickableVars;
    }

    @Override
    public boolean isPicked(Var var) {
        if (isValidBuild()) {
            return fixResult.isPicked(var);
        } else {
            throw new IllegalStateException("Calling isPicked(Var var) is only allowed if isValidBuild() returns true");
        }
    }

    @Override
    public boolean isUiPicked(Var var) {
        assert uiVars.contains(var) : "var[" + var + "] is not a uiVar";
        if (isValidBuild()) {
            return fixResult.isPicked(var);
        } else {
            return currentTrueUiVars.contains(var);
        }
    }


    @Override
    public FixResult proposePickRadio(Var var) {
        Preconditions.checkArgument(uiVars.contains(var), "var[" + var + "] is not a uiVar");
        Preconditions.checkArgument(var.isXorChild());

        ImmutableSet<Var> siblings = var.getSiblings();
        ImmutableSet.Builder<Var> builder = ImmutableSet.builder();

        for (Var v : currentTrueUiVars) {
            if (!siblings.contains(v)) {
                builder.add(var);
            }
        }

        builder.add(var);

        ImmutableSet<Var> copy = builder.build();

        return featureModel.fixup(copy);
    }

    @Override
    public FixResult proposePickRadio(String varCode) {
        Var var = featureModel.getVarOrNull(varCode);
        assert var != null;
        return proposePickRadio(var);
    }

    @Override
    public FixResult proposeToggleCheckBox(String varCode) {
        Var var = featureModel.getVarOrNull(varCode);
        assert var != null;
        return proposeToggleCheckBox(var);
    }

    @Override
    public FixResult proposeToggleCheckBox(Var var) {
        assert !var.isXorChild();

        ImmutableSet.Builder<Var> builder = ImmutableSet.builder();
        ImmutableSet<Var> copy;
        if (currentTrueUiVars.contains(var)) {
            for (Var v : currentTrueUiVars) {
                if (!v.equals(var)) {
                    builder.add(var);
                }
            }
        } else {
            builder.addAll(currentTrueUiVars);
            builder.add(var);
            copy = builder.build();
        }

        copy = builder.build();

        return featureModel.fixup(copy);
    }

    @Override
    public void pickRadio(Var var) {
        assert uiVars.contains(var) : "var[" + var + "] is not a uiVar";
        assert var.isXorChild();
        Var xorParent = var.getParent();
        List<Var> childVars = xorParent.getChildVars();
        currentTrueUiVars.removeAll(childVars);
        currentTrueUiVars.add(var);

        potentialBlinkVar = null;
    }

    public void pickRadio(String varCode) {
        Var var = featureModel.getVarOrNull(varCode);
        assert var != null;
        pickRadio(var);
    }

    @Override
    public void toggleCheckBox(Var var) {
        assert uiVars.contains(var) : "var[" + var + "] is not a uiVar";
        assert !var.isXorChild();

        if (currentTrueUiVars.contains(var)) {
            currentTrueUiVars.remove(var);
            potentialBlinkVar = null;
        } else {
            currentTrueUiVars.add(var);
            potentialBlinkVar = var;
        }

    }

    @Override
    public void toggleCheckBox(String varCode) {
        Var var = featureModel.getVarOrNull(varCode);
        assert var != null;
        toggleCheckBox(var);
    }

//    private Csp toCsp() throws AssignmentException {
//
//        assert currentSlice != null;
//
//        Csp csp = featureModel.createCsp();
//
//        for (Var trueVar : currentTrueVars) {
//            csp.assignTrue(trueVar);
//        }
//
//        return csp;
//
//    }


    public void fix() {
        ImmutableSet<Var> picks = ImmutableSet.copyOf(currentTrueUiVars);
        this.fixResult = featureModel.fixup(picks);
    }


    @Override
    public boolean isInvalidBuild() {
        return fixResult.isInvalidBuild();
    }

    @Override
    public boolean isValidBuild() {
        return fixResult.isValidBuild();
    }

    @Override
    public String getErrorMessage() {
        return fixResult.getErrorMessage();
    }

    @Override
    public AssignmentException getException() {
        return fixResult.getException();
    }

    public void print() {
        System.err.println("Current UI Picks: ");
        System.err.println("\t Picks: \t" + currentTrueUiVars);

        if (isInvalidBuild()) {
            System.err.println("\t Error: \t" + this.getException().getMessage());
        } else {
            System.err.println("\t Fixed: \t" + getFixedPicks());
        }
        System.err.println();

    }

    public void addPicksChangeHandler(UiPicksChangeHandler picksChangeHandler) {
        bus.addHandler(UiPicksChangeEvent.TYPE, picksChangeHandler);
    }

    public void fire() {
        bus.fireEventFromSource(new UiPicksChangeEvent(), this);
    }

    public Set<Var> getCurrentTrueUiVars() {
        return Collections.unmodifiableSet(currentTrueUiVars);
    }

    public Set<Var> getFixedPicks() {
        assert fixResult != null;
        assert fixResult.isValidBuild();
        return Collections.unmodifiableSet(fixResult.getAssignments().getTrueVars());
    }

    public FixResult getFixResult() {
        return new FixResult(fixResult);
    }

    public Var getPotentialBlinkVar() {
        return potentialBlinkVar;
    }


}
