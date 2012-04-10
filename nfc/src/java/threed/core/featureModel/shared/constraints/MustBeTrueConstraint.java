package threed.core.featureModel.shared.constraints;

import threed.core.featureModel.shared.boolExpr.BoolExpr;

public class MustBeTrueConstraint implements Constraint {

    private final BoolExpr expr;

    public MustBeTrueConstraint(BoolExpr expr) {
        this.expr = expr;
    }

    public BoolExpr getExpr() {
        return expr;
    }

}
