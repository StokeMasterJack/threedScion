package com.tms.threed.threedCore.featureModel.shared.boolExpr;

public class DependsOnVisitor extends BoolExprVisitor {

    private Var var;
    private boolean response = false;
    private boolean complete = false;

    public DependsOnVisitor(Var var) {
        this.var = var;
    }

    @Override protected void visitImpl(Junction junction) {
        if (complete) return;
        for (BoolExpr expr : junction.expressions) {
            if (complete) return;
            expr.accept(this);
        }
    }

    @Override protected void visitImpl(Pair pair) {
        if (complete) return;
        pair.getExpr1().accept(this);
        if (complete) return;
        pair.getExpr2().accept(this);
    }

    @Override protected void visitImpl(Unary unary) {
        if (complete) return;
        unary.getExpr().accept(this);
    }

    @Override protected void visitImpl(Constant constant) {

    }


    @Override protected void visitImpl(Var var) {
        if (this.var == var) {
            response = true;
            complete = true;
        }
    }

    public boolean getResponse() {
        return response;
    }
}