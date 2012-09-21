package c3i.core.featureModel.shared.boolExpr;

import java.util.HashSet;
import java.util.Set;

public class GetLeafVarsVisitor extends BoolExprVisitor {

    private Set<Var> leafVars = new HashSet<Var>();

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
        if (var.isLeaf()) leafVars.add(var);
    }

    public Set<Var> getLeafVars() {
        return leafVars;
    }

    public static Set<Var> getLeafVars(BoolExpr e) {
        GetLeafVarsVisitor v = new GetLeafVarsVisitor();
        e.accept(v);
        return v.getLeafVars();
    }
}
