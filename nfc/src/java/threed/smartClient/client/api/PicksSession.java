package threed.smartClient.client.api;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.gwt.core.client.Scheduler;
import threed.core.featureModel.shared.FixResult;
import threed.core.featureModel.shared.boolExpr.Var;
import threed.core.imageModel.shared.slice.SimplePicks;
import threed.core.threedModel.shared.ThreedModel;
import smartsoft.util.gwt.client.events3.ChangeListener;
import smartsoft.util.gwt.client.events3.ChangeTopic;

public class PicksSession {

    private final ThreedModel threedModel;

    private final ChangeTopic<PicksSession, FixResult> changeTopic = new ChangeTopic(this);

    private ImmutableSet<Var> picks;
    private FixResult fixedPicks;

    public PicksSession(ThreedModel threedModel) {
        this.threedModel = threedModel;
    }

    public void setPicksRaw(final Iterable<String> newValue) {
        Preconditions.checkNotNull(newValue);
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                ImmutableSet<Var> picks = threedModel.varCodesToVars(newValue);
                setPicksInternal(picks);
            }
        });
    }

    public void setPicks(ImmutableSet<Var> newValue) {
        setPicksInternal(newValue);
    }

    public void setPicksFixed(FixResult newValue) {
        this.picks = null;
        setPicksFixedInternal(newValue);
    }

    private void setPicksInternal(final ImmutableSet<Var> newValue) {
        Preconditions.checkNotNull(newValue);
        ImmutableSet<Var> oldValue = this.picks;
        if (!Objects.equal(oldValue, newValue)) {
            this.picks = newValue;
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    FixResult fixResult = threedModel.fixup(newValue);
                    setPicksFixedInternal(fixResult);
                }
            });

        }
    }


    private void setPicksFixedInternal(FixResult newValue) {
        Preconditions.checkNotNull(newValue);
        FixResult oldValue = this.fixedPicks;
        if (!Objects.equal(oldValue, newValue)) {
            this.fixedPicks = newValue;
            changeTopic.fire(oldValue, newValue);
        }
    }


    public void addChangeListener(ChangeListener<PicksSession, FixResult> listener) {
        changeTopic.addListener(listener);
    }

    public SimplePicks getSimplePicks() {
        return fixedPicks;
    }
}
