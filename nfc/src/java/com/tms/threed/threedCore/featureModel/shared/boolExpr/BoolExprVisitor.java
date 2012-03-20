package com.tms.threed.threedCore.featureModel.shared.boolExpr;

import java.util.HashSet;
import java.util.Set;

public abstract class BoolExprVisitor {

    protected Set<BoolExpr> visited = new HashSet<BoolExpr>();

    abstract protected void visitImpl(Junction junction);

    abstract protected void visitImpl(Pair pair);

    abstract protected void visitImpl(Unary unary);

    abstract protected void visitImpl(Constant constant);
    abstract protected void visitImpl(Var var);

    public void visit(Junction e) {
        if (visited.contains(e)) return;
        visitImpl(e);
        visited.add(e);
    }



    public void visit(Pair e) {
        if (visited.contains(e)) return;
        visitImpl(e);
        visited.add(e);
    }

    public void visit(Unary e) {
        if (visited.contains(e)) return;
        visitImpl(e);
        visited.add(e);
    }

     public void visit(Var e) {
        if (visited.contains(e)) return;
        visitImpl(e);
        visited.add(e);
    }

    public void visit(Constant e) {
        if (visited.contains(e)) return;
        visitImpl(e);
        visited.add(e);
    }


}
