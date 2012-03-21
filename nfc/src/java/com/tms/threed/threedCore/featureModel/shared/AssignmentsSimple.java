package com.tms.threed.threedCore.featureModel.shared;

import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;

/**
 * This class is strictly <i>one-way</i>: use can never reassign or unassign variable - you can only assign
 */
public class AssignmentsSimple extends AbstractAssignments<AssignmentsSimple> {

    public AssignmentsSimple(Vars vars) {
        super(vars);
    }

    public AssignmentsSimple(AssignmentsSimple that) {
        super(that);
    }

    @Override
    protected boolean removeFromOpenVars(Var var) {
        return true;
    }

}