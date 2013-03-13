package c3i.smartClient.client.model;

import c3i.featureModel.shared.FixedPicks;
import c3i.featureModel.shared.boolExpr.Var;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.util.shared.events.ChangeListener;
import c3i.util.shared.futures.AsyncFunction;
import c3i.util.shared.futures.AsyncKeyValue;
import c3i.util.shared.futures.Completer;
import c3i.util.shared.futures.RValue;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.gwt.core.client.Scheduler;

import java.util.Set;

public class PicksSession implements RValue<FixedPicks> {

    private final ThreedModel threedModel;

    private final AsyncKeyValue<Set<Var>, FixedPicks> fixedPicks;


    public PicksSession(ThreedModel threedModel) {
        this.threedModel = threedModel;
        Set<Var> initPicks = threedModel.getFeatureModel().getInitiallyTruePickableVars();
        AsyncFunction<Set<Var>, FixedPicks> asyncFunction = createAsyncFunction(threedModel);
        fixedPicks = new AsyncKeyValue<Set<Var>, FixedPicks>("RawPicks", "FixedPicks", asyncFunction, initPicks);
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
    public void removeAll() {
        fixedPicks.removeAll();
    }

    public void setPicksRaw(final Iterable<String> newValue) {
        Preconditions.checkNotNull(newValue);
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                ImmutableSet<Var> picks = threedModel.varCodesToVars(newValue);
                setPicks(picks);
            }
        });
    }

    public void setPicks(final Set<Var> newValue) {
        fixedPicks.setKey(newValue);
    }

    public void set(FixedPicks newValue) {
        Preconditions.checkNotNull(newValue);
        fixedPicks.setKeyAndValue(newValue.getPicks(), newValue);
    }

    public void addChangeListener(ChangeListener<FixedPicks> listener) {
        fixedPicks.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener<FixedPicks> listener) {
        fixedPicks.removeChangeListener(listener);
    }

    public FixedPicks get() {
        return fixedPicks.get();
    }

    @Override
    public boolean isEmpty() {
        return get() == null;
    }
}
