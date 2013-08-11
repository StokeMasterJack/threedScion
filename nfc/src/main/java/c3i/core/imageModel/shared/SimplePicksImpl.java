package c3i.core.imageModel.shared;

import c3i.core.featureModel.shared.Assignments;
import c3i.core.featureModel.shared.boolExpr.Var;

public class SimplePicksImpl implements SimplePicks {

    private final Assignments assignments;

    public SimplePicksImpl(Assignments assignments) {
        this.assignments = assignments;
    }

    @Override
    public boolean isPicked(Var var) {
        return assignments.isPicked(var);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimplePicksImpl that = (SimplePicksImpl) o;
        return assignments.equals(that.assignments);

    }

    @Override
    public int hashCode() {
        return assignments.hashCode();
    }
}
