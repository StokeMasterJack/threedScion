package c3i.featureModel.shared.dirtyQueue;

import c3i.featureModel.shared.boolExpr.HasChildContent;

public class ClauseSimplified extends CspChangeEvent {

    private final HasChildContent<?> before;
    private final HasChildContent<?> after;

    public ClauseSimplified(HasChildContent<?> before, HasChildContent<?> after) {
        this.before = before;
        this.after = after;
    }

    public HasChildContent<?> getBefore() {
        return before;
    }

    public HasChildContent<?> getAfter() {
        return after;
    }
}
