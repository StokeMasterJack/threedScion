package com.tms.threed.threedCore.featureModel.shared.boolExpr;

import java.util.Collection;
import java.util.LinkedHashSet;

public class Expressions extends LinkedHashSet<BoolExpr> {

    public Expressions() {
    }

    public Expressions(Collection<BoolExpr> boolExprs) {
        for (BoolExpr expr : boolExprs) {
            this.add(expr);
        }
    }

    public Expressions(BoolExpr boolExpr) {
        this.add(boolExpr);

    }

    public BoolExpr getFirst() {
        return iterator().next();
    }

    @Override
    public boolean add(BoolExpr expr) throws NegatingVarsException {
        assert expr != null;

        if (expr.isVar()) {
            Var var = expr.asVar();
            if (this.contains(var.compliment)) throw new NegatingVarsException();
        } else if (expr.isNegatedVar()) {
            Not not = expr.asNot();
            Var var = not.getExpr().asVar();
            if (this.contains(var)) throw new NegatingVarsException();
        }

        return super.add(expr);
    }
}
