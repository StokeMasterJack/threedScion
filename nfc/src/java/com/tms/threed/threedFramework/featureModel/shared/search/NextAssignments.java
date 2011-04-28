package com.tms.threed.threedFramework.featureModel.shared.search;

import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;

public class NextAssignments {

    private final Var var;

    public NextAssignments(Var var) {
        this.var = var;
    }

    public boolean getFirstAssignment() {
        return true;
    }

    public boolean getSecondAssignment() {
        return false;
    }

}
