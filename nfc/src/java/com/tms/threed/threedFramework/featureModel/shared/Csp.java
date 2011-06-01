package com.tms.threed.threedFramework.featureModel.shared;

import com.tms.threed.threedFramework.featureModel.shared.boolExpr.MasterConstraint;

public class Csp extends AbstractCsp<AssignmentsSimple, Csp> {

    private final AssignmentsSimple assignments;

    public Csp(Vars vars, MasterConstraint constraint) {
        super(vars, constraint);
        this.assignments = new AssignmentsSimple(vars);
    }

    public Csp(Csp that) {
        super(that);
        this.assignments = new AssignmentsSimple(that.assignments);
    }

    @Override
    public AssignmentsSimple getAssignments() {
        return assignments;
    }

    @Override
    public Csp copy() {
        return new Csp(this);
    }
}