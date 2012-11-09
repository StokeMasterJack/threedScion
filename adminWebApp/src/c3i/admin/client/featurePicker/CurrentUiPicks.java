package c3i.admin.client.featurePicker;

import c3i.util.shared.futures.AsyncKeyValue;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import c3i.util.shared.futures.Completer;
import smartsoft.util.gwt.client.Console;
import c3i.util.shared.events.ChangeListener;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.featureModel.shared.FixedPicks;
import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.util.shared.futures.RValue;
import c3i.util.shared.futures.AsyncFunction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CurrentUiPicks implements UiPicks, RValue<FixedPicks> {

    private final ThreedModel threedModel;
    private final FeatureModel featureModel;
    private final Set<Var> uiVars;

    private HashSet<Var> currentTrueUiVars;

    private final AsyncKeyValue<Set<Var>, FixedPicks> fixedPicks;

    private Var potentialBlinkVar;

    public CurrentUiPicks(ThreedModel threedModel) {
        this.threedModel = threedModel;
        this.featureModel = threedModel.getFeatureModel();
        this.uiVars = featureModel.getPickableVars();


        currentTrueUiVars = Sets.newHashSet(featureModel.getInitiallyTruePickableVars());

        fixedPicks = new AsyncKeyValue<Set<Var>, FixedPicks>(createAsyncFunction(threedModel));

        updateFixedPicksKey();

    }

    public AsyncKeyValue<Set<Var>, FixedPicks> getFixedPicks() {
        return fixedPicks;
    }

    private static AsyncFunction<Set<Var>, FixedPicks> createAsyncFunction(final ThreedModel threedModel) {
        return new AsyncFunction<Set<Var>, FixedPicks>() {
            @Override
            public void start(Set<Var> input, Completer<FixedPicks> completer) throws Exception {
                FixedPicks fixup = threedModel.fixup(input);
                completer.setResult(fixup);
            }
        };
    }

    @Override
    public boolean isUiPicked(Var var) {
        Preconditions.checkArgument(uiVars.contains(var), "var[" + var + "] is not a uiVar");
        return currentTrueUiVars.contains(var);
    }


    @Override
    public FixedPicks proposePickRadio(Var var) {
        Preconditions.checkArgument(uiVars.contains(var), "var[" + var + "] is not a uiVar");
        Preconditions.checkArgument(var.isXorChild());

        ImmutableSet<Var> siblings = var.getSiblings();

        HashSet<Var> copy = (HashSet<Var>) currentTrueUiVars.clone();
        copy.removeAll(siblings);
        copy.add(var);

        return featureModel.fixup(copy);
    }

    @Override
    public FixedPicks proposePickRadio(String varCode) {
        Var var = featureModel.getVarOrNull(varCode);
        if (var == null) {
            throw new IllegalStateException();
        }
        return proposePickRadio(var);
    }

    @Override
    public FixedPicks proposeToggleCheckBox(String varCode) {
        Var var = featureModel.getVarOrNull(varCode);
        if (var == null) {
            throw new IllegalStateException();
        }
        return proposeToggleCheckBox(var);
    }

    @Override
    public FixedPicks proposeToggleCheckBox(Var var) {
        Preconditions.checkArgument(!var.isXorChild());

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
        Preconditions.checkArgument(uiVars.contains(var), "var[" + var + "] is not a uiVar");
        Preconditions.checkArgument(var.isXorChild());


        Var xorParent = var.getParent();
        List<Var> childVars = xorParent.getChildVars();
        currentTrueUiVars.removeAll(childVars);
        currentTrueUiVars.add(var);

        updateFixedPicksKey();

        potentialBlinkVar = null;
    }

    public void pickRadio(String varCode) {
        Var var = featureModel.getVarOrNull(varCode);
        Preconditions.checkNotNull(var);
        pickRadio(var);
    }

    private void updateFixedPicksKey() {
        Preconditions.checkNotNull(currentTrueUiVars);
        try {
            ImmutableSet<Var> newKey = ImmutableSet.copyOf(currentTrueUiVars);
            Preconditions.checkNotNull(newKey);
            fixedPicks.setKey(newKey);
        } catch (Throwable e) {
            Console.error(e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void toggleCheckBox(Var var) {
        Preconditions.checkArgument(uiVars.contains(var), "var[" + var + "] is not a uiVar");
        Preconditions.checkArgument(!var.isXorChild());

        if (currentTrueUiVars.contains(var)) {
            currentTrueUiVars.remove(var);
            potentialBlinkVar = null;
        } else {
            currentTrueUiVars.add(var);
            potentialBlinkVar = var;
        }

        updateFixedPicksKey();

    }

    @Override
    public void toggleCheckBox(String varCode) {
        Var var = featureModel.getVarOrNull(varCode);
        Preconditions.checkArgument(var != null);
        toggleCheckBox(var);
    }


    public Set<Var> getCurrentTrueUiVars() {
        return ImmutableSet.copyOf(currentTrueUiVars);
    }

//    public FixedPicks fixSync() {
//        if (fixedPicks.get() == null || fixedPicks.isDirty()) {
//            return featureModel.fixup(currentTrueUiVars);
//        } else {
//            return fixedPicks.get();
//        }
//    }

    public Var getPotentialBlinkVar() {
        return potentialBlinkVar;
    }

    public void setPicksRaw(Iterable<String> newValue) {
        ImmutableSet<Var> newPicks = featureModel.varCodesToVars(newValue);
        setPicks(newPicks);
    }

    public void setPicks(Set<Var> newPicks) {
        this.currentTrueUiVars = new HashSet<Var>(newPicks);
        updateFixedPicksKey();
    }

    public void set(FixedPicks newValue) {
        Set<Var> picks = newValue.getPicks();
        fixedPicks.setKeyAndValue(picks, newValue);
    }

    public void addChangeListener(final ChangeListener<FixedPicks> listener) {
        fixedPicks.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener<FixedPicks> listener) {
        fixedPicks.removeChangeListener(listener);
    }

    public FixedPicks get() {
        return fixedPicks.get();
    }

    @Override
    public void removeAll() {


    }

    @Override
    public boolean isEmpty() {
        return fixedPicks.isEmpty();
    }
}
