package c3i.core.featureModel.shared;

import c3i.core.featureModel.shared.boolExpr.MasterConstraint;

public class CspSimple extends Csp<AssignmentsSimple, CspSimple> {

    private final AssignmentsSimple assignments;

    public CspSimple(Vars vars, MasterConstraint constraint) {
        super(vars, constraint);
        this.assignments = new AssignmentsSimple(vars);
    }

    public CspSimple(CspSimple that) {
        super(that);
        this.assignments = new AssignmentsSimple(that.assignments);
    }

    @Override
    public AssignmentsSimple getAssignments() {
        return assignments;
    }

    @Override
    public CspSimple copy() {
        return new CspSimple(this);
    }
}
