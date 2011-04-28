package com.tms.threed.threedFramework.featureModel.shared.search.decision;

import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;

public abstract class SimpleAssignment implements Decision {

    protected final Var var;

    public SimpleAssignment(Var var) {
        this.var = var;
    }


}
