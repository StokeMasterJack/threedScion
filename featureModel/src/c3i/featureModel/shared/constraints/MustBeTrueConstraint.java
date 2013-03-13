package c3i.featureModel.shared.constraints;

import c3i.featureModel.shared.boolExpr.BoolExpr;

public class MustBeTrueConstraint implements Constraint {

    private final BoolExpr expr;

    public MustBeTrueConstraint(BoolExpr expr) {
        this.expr = expr;
    }

    public BoolExpr getExpr() {
        return expr;
    }

}
