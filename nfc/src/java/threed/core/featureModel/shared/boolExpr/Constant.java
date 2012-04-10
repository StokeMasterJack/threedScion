package threed.core.featureModel.shared.boolExpr;

import threed.core.featureModel.shared.AutoAssignContext;

import java.util.Collection;
import java.util.Collections;

public abstract class Constant extends BoolExpr {

    public void accept(BoolExprVisitor visitor) {
        visitor.visit(this);
    }


    @Override
    public int getExprCount() {
        return 1;
    }

    @Override
    public Collection<BoolExpr> getExpressions() {
        return Collections.emptySet();
    }

    @Override
    public BoolExpr simplify(AutoAssignContext ctx) {
        return this;
    }


    @Override
    public final BoolExpr toCnf(AutoAssignContext ctx) {
        return this;
    }

    @Override
    public int getDeepExpressionCount() {
        return 1;
    }

    @Override
    final public boolean containsDeep(Var v) {
        return false;
    }

    @Override
    final public boolean containsShallow(Var v) {
        return false;
    }

    @Override
    public final boolean containsShallow(Constant c) {
        return false;
    }

    @Override
    public boolean containsVarCodeDeep(String varCode) {
        return false;
    }

    @Override
    public int occurranceCount(Var var) {
        return 0;
    }

    @Override
    public BoolExpr cleanOutIffVars(IffContext ctx) {
        return this;
    }

    @Override
    public BoolExpr copy() {
        return this;
    }

    @Override
    public String toString(AutoAssignContext ctx) {
        return toString();
    }
}

