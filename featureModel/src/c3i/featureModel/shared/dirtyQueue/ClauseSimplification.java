package c3i.featureModel.shared.dirtyQueue;

import c3i.featureModel.shared.boolExpr.HasChildContent;

public class ClauseSimplification extends CspChangeEvent {

    private final HasChildContent<?> before;
    private HasChildContent<?> after;

    public ClauseSimplification(HasChildContent<?> before) {
        this.before = before;
        this.after = null;
    }

    public HasChildContent<?> getBefore() {
        return before;
    }

    public HasChildContent<?> getAfter() {
        return after;
    }

    public void executeSimplify() {

    }
}
