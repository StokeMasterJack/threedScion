package c3i.featureModel.shared.boolExpr;

import c3i.featureModel.shared.AutoAssignContext;
import smartsoft.util.shared.ImmutableCollection;

import java.util.Collection;
import java.util.Iterator;

public abstract class Unary extends HasChildContent<BoolExpr> {

    protected final BoolExpr expr;

    Unary(BoolExpr expr) {
        this.expr = expr;
    }


    public void accept(BoolExprVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int getExprCount() {
        return 1;
    }

    public abstract String getSymbol();

    public BoolExpr getExpr() {
        return expr;
    }

    @Override
    public final String toString() {
        return getSymbol() + expr;
    }

    @Override
    public String toString(AutoAssignContext ctx) {
        return getSymbol() + expr.simplify(ctx);
    }

    @Override
    public Collection<BoolExpr> getExpressions() {
        return unaryCollection;
    }

    @Override
    public boolean containsShallow(Var v) {
        return v.equals(expr);
    }

    @Override
    public boolean containsDeep(Var v) {
        return v.equals(expr) || this.expr.containsDeep(v);
    }


    @Override
    public boolean containsShallow(Constant c) {
        return expr.equals(c);
    }

    @Override
    public boolean containsVarCodeDeep(String varCode) {
        return expr.containsVarCodeDeep(varCode);
    }

    @Override
    public int occurranceCount(Var var) {
        return var.occurranceCount(var);
    }

    private class UnaryExpressionIterator implements Iterator<BoolExpr> {

        private boolean hasNextBeenCalled;

        @Override
        public boolean hasNext() {
            return !hasNextBeenCalled;
        }

        @Override
        public BoolExpr next() {
            if (hasNextBeenCalled) {
                throw new IllegalStateException();
            } else {
                hasNextBeenCalled = true;
                return expr;
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private final UnaryCollection unaryCollection = new UnaryCollection();

    private class UnaryCollection extends ImmutableCollection<BoolExpr> {

        @Override
        public int size() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }


        @Override
        public Iterator<BoolExpr> iterator() {
            return new UnaryExpressionIterator();
        }

        @Override
        public BoolExpr[] toArray() {
            return new BoolExpr[]{expr};
        }

        @Override
        public <T> T[] toArray(T[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(Object o) {
            return expr.equals(o);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            for (Object o : c) {
                if (!this.contains(o)) return false;
            }
            return true;
        }

    }


}
