package threed.core.featureModel.shared.boolExpr;

public class CheckForNonLeafReferenceVisitor extends BoolExprVisitor {

    private boolean response = false;
    private boolean complete = false;

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
        if (!var.isLeaf()) {
            response = true;
            complete = true;
        }
    }

    public boolean getResponse() {
        return response;
    }
}