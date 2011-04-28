package com.tms.threed.threedFramework.featureModel.shared.boolExpr;

import java.util.HashSet;
import java.util.Set;

public class GetCareVarsVisitor extends BoolExprVisitor {

    private Set<Var> careSet = new HashSet<Var>();

    @Override protected void visitImpl(Junction junction) {
        for (BoolExpr expr : junction.expressions) {
            expr.accept(this);
        }
    }

    @Override protected void visitImpl(Pair pair) {
        pair.getExpr1().accept(this);
        pair.getExpr2().accept(this);
    }

    @Override protected void visitImpl(Unary unary) {
        unary.getExpr().accept(this);
    }

    @Override protected void visitImpl(Constant constant) {

    }


    @Override protected void visitImpl(Var var) {
        careSet.add(var);
    }

    public Set<Var> getCareSet() {
        return careSet;
    }

    public static Set<Var> getCareVars(BoolExpr e) {
        GetCareVarsVisitor v = new GetCareVarsVisitor();
        e.accept(v);
        return v.getCareSet();
    }
}