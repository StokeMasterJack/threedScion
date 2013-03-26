package c3i.featureModel.shared.boolExpr;

import c3i.featureModel.shared.AutoAssignContext;
import c3i.featureModel.shared.Bit;
import c3i.featureModel.shared.EvalContext;
import c3i.featureModel.shared.Tri;
import smartsoft.util.shared.ImmutableCollection;

import java.util.Collection;
import java.util.Iterator;


public class Not extends Unary {

    public static final Type TYPE = Type.Not;

    private final int hash;

    protected final BoolExpr expr;

    public Not(BoolExpr expr) {
        this.expr = expr;
        this.hash = 31 * TYPE.id + expr.hashCode();
    }

    public void accept(BoolExprVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int getExprCount() {
        return 1;
    }

    @Override
    public final String toString() {
        return getSymbol() + expr;
    }

    public BoolExpr getExpr() {
        return expr;
    }

    public Type getType() {
        return TYPE;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public String getSymbol() {
        return "!";
    }

    @Override
    public boolean containsVar(Var v) {
        return expr.containsVar(v);
    }

    @Override
    public void autoAssignTrue(AutoAssignContext ctx) {
        getExpr().autoAssignFalse(ctx);
    }

    @Override
    public void autoAssignFalse(AutoAssignContext ctx) {
        getExpr().autoAssignTrue(ctx);
    }

    @Override
    public Tri eval(EvalContext ctx) {
        Tri val = expr.eval(ctx);
        if (val.isFalse()) return Bit.TRUE;
        if (val.isTrue()) return Bit.FALSE;
        else return Bit.OPEN;
    }

    @Override
    public BoolExpr simplify(AutoAssignContext ctx) {

//        if (true) return this;

        BoolExpr e = getExpr();
        BoolExpr v = e.simplify(ctx);

        if (v.isFalse()) {
            return TRUE;
        } else if (v.isTrue()) {
            return FALSE;
        } else if (v.isNot()) {
            return v.getExpr().simplify(ctx);
        } else if (v == expr) {
            return this;
        } else if (v == e) {
            return this;
        } else {
            return new Not(v);
        }
    }

    /**
     * each NOT constraint ¬x = y by the clauses x ∨ y,¬x ∨ ¬y
     */
    @Override
    public BoolExpr toCnf(AutoAssignContext ctx) {

        BoolExpr simple = simplify(ctx);
        if (simple != this) return simple.toCnf(ctx);

        BoolExpr e = getExpr();

        BoolExpr v = e.toCnf(ctx);

        if (v != e) {
            return new Not(v).toCnf(ctx);
        }


        if (e.isAnd()) {
            Collection<BoolExpr> andTerms = e.getExpressions();
            Expressions orTerms = new Expressions();
            for (BoolExpr andTerm : andTerms) {
                orTerms.add(not(andTerm));
            }
            return or(orTerms);
        } else if (e.isOr()) {
            Collection<BoolExpr> orTerms = e.getExpressions();
            Expressions andTerms = new Expressions();
            for (BoolExpr orTerm : orTerms) {
                andTerms.add(not(orTerm));
            }
            return and(andTerms).toCnf(ctx);
        } else {
            return this;
        }
    }


    @Override
    public int getDeepExpressionCount() {
        return 1;
    }

    @Override
    public boolean isLiteral() {
        return expr.isVar();
    }

    @Override
    public boolean isNegatedVar() {
        return expr.isVar();
    }


    @Override
    public Var asVar() {
        return expr.asVar();
    }

    @Override
    public BoolExpr cleanOutIffVars(IffContext ctx) {
        BoolExpr ee = ctx.getReplacement(expr);
        return new Not(ee.cleanOutIffVars(ctx));
    }

    @Override
    public BoolExpr copy() {
        BoolExpr e = getExpr();
        BoolExpr c = e.copy();
        return new Not(c);
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

