package c3i.featureModel.shared.dirtyQueue;

import c3i.featureModel.shared.boolExpr.HasChildContent;

public class ClauseRemoved extends CspChangeEvent {

    private final HasChildContent<?> removedClause;

    public ClauseRemoved(HasChildContent<?> removedClause) {
        this.removedClause = removedClause;
    }

    public HasChildContent<?> getRemovedClause() {
        return removedClause;
    }

}
