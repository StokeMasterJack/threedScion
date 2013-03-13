package c3i.featureModel.shared;

import c3i.featureModel.shared.boolExpr.BoolExpr;

public class Reduction {

    public final BoolExpr expr;
    public final boolean value;

    public Reduction(BoolExpr expr, boolean value) {
        this.expr = expr;
        this.value = value;
    }

    public BoolExpr getExpr() {
        return expr;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return expr + " => " + value;
    }
}
