package com.tms.threed.threedCore.featureModel.shared.search.decision;

import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;

public abstract class SimpleAssignment implements Decision {

    protected final Var var;

    public SimpleAssignment(Var var) {
        this.var = var;
    }


}
