package com.tms.threed.threedFramework.featureModel.shared.constraints;

import com.tms.threed.threedFramework.featureModel.shared.boolExpr.BoolExpr;

public class MustBeTrueConstraint implements Constraint {

    private final BoolExpr expr;

    public MustBeTrueConstraint(BoolExpr expr) {
        this.expr = expr;
    }

    public BoolExpr getExpr() {
        return expr;
    }

}
