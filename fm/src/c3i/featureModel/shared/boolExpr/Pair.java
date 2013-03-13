package c3i.featureModel.shared.boolExpr;

import c3i.featureModel.shared.AutoAssignContext;
import smartsoft.util.shared.ImmutableCollection;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

public abstract class Pair extends HasChildContent<Pair.ContentPair> {

    protected final ContentPair content;

    Pair(ContentPair content) {
        this.content = content;
    }

    public void accept(BoolExprVisitor visitor) {
        visitor.visit(this);
    }

    public abstract String getSymbol();

    public final String toString() {
        assert content != null;
        return "(" + content.expr1 + " " + getSymbol() + " " + content.expr2 + ")";
    }

    @Override
    public String toString(AutoAssignContext ctx) {
        assert content != null;
        return "(" + content.expr1.simplify(ctx) + " " + getSymbol() + " " + content.expr2.simplify(ctx) + ")";
    }

    public BoolExpr getExpr1() {
        return content.expr1;
    }

    public BoolExpr getExpr2() {
        return content.expr2;
    }

    @Override
    public int getExprCount() {
        return 2;
    }

    private class PairExpressionIterator implements Iterator<BoolExpr> {

        private int howManyTimesHasNextBeenCalled;

        @Override
        public boolean hasNext() {
            return howManyTimesHasNextBeenCalled < 2;
        }

        @Override
        public BoolExpr next() {
            BoolExpr retVal;

            if (howManyTimesHasNextBeenCalled == 0) retVal = content.expr1;
            else if (howManyTimesHasNextBeenCalled == 1) retVal = content.expr2;
            else throw new IllegalStateException();

            howManyTimesHasNextBeenCalled++;
            return retVal;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private class PairCollection extends ImmutableCollection<BoolExpr> {

        @Override
        public int size() {
            return 2;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }


        @Override
        public Iterator<BoolExpr> iterator() {
            return new PairExpressionIterator();
        }


        @Override
        public BoolExpr[] toArray() {
            return new BoolExpr[]{content.expr1, content.expr2};
        }

        @Override
        public <T> T[] toArray(T[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(Object o) {
            return content.expr1.equals(o) || content.expr2.equals(o);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            for (Object o : c) {
                if (!this.contains(o)) return false;
            }
            return true;
        }

    }

    @Override
    public int getDeepExpressionCount() {
        return getExprCount() + content.expr1.getDeepExpressionCount() + content.expr2.getDeepExpressionCount();
    }

    @Override
    public boolean containsVarCodeDeep(String varCode) {
        return content.expr1.containsVarCodeDeep(varCode) || content.expr2.containsVarCodeDeep(varCode);
    }

    @Override
    public int occurranceCount(Var var) {
        return content.expr1.occurranceCount(var) + content.expr2.occurranceCount(var);
    }

    public static int calcHash(int nodeId1, int nodeId2) {
        int hashDave = nodeId1 * 71 + nodeId2;

        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).
        hashDave ^= (hashDave >>> 20) ^ (hashDave >>> 12);
        return hashDave ^ (hashDave >>> 7) ^ (hashDave >>> 4);
    }


    protected BoolExpr create(Class<? extends Pair> cls, BoolExpr expr1, BoolExpr expr2) {
        if (cls == Imp.class) return new Imp(expr1, expr2);
        else if (cls == Conflict.class) return new Conflict(expr1, expr2);
        else if (cls == Iff.class) return new Iff(expr1, expr2);
        else throw new IllegalStateException();
    }

    @Override
    public boolean containsShallow(Var var) {
        return var.equals(content.expr1) || var.equals(content.expr2);
    }

    @Override
    public boolean containsShallow(Constant c) {
        return content.expr1 == c || content.expr2 == c;
    }

    @Override
    public boolean containsDeep(Var var) {
        return containsShallow(var) || content.expr1.containsDeep(var) || content.expr2.containsDeep(var);
    }


    @Override
    public BoolExpr cleanOutIffVars(IffContext ctx) {
        BoolExpr ee1 = ctx.getReplacement(content.expr1);
        BoolExpr ee2 = ctx.getReplacement(content.expr2);
        return create(getClass(), ee1.cleanOutIffVars(ctx), ee2.cleanOutIffVars(ctx));
    }

    public ContentPair getContent() {
        return content;
    }

    public abstract static class ContentPair {

        protected final BoolExpr expr1;
        protected final BoolExpr expr2;

        protected ContentPair(BoolExpr expr1, BoolExpr expr2) {
            assert expr1 != null;
            assert expr2 != null;
            this.expr1 = expr1;
            this.expr2 = expr2;
        }

        @Override
        public int hashCode() {
            return 31 * expr1.hashCode() + expr2.hashCode();
        }

        @Override
        public abstract boolean equals(Object o);


    }

    public static class UnorderedPair extends ContentPair {

        public UnorderedPair(BoolExpr expr1, BoolExpr expr2) {
            super(expr1, expr2);
        }

        @Override
        public boolean equals(Object o) {
            ContentPair that = (ContentPair) o;
            boolean eq1 = expr1.equals(that.expr1) && expr2.equals(that.expr2);
            boolean eq2 = expr1.equals(that.expr2) && expr2.equals(that.expr1);
            return eq1 || eq2;
        }

    }

    public static class OrderedPair extends ContentPair {

        public OrderedPair(BoolExpr expr1, BoolExpr expr2) {
            super(expr1, expr2);
        }

        @Override
        public boolean equals(Object o) {
            ContentPair that = (ContentPair) o;
            boolean t1 = expr1.equals(that.expr1);
            boolean t2 = expr2.equals(that.expr2);
            boolean retVal = t1 && t2;
            return retVal;
        }

    }


    private final PairCollection pairCollection = new PairCollection();

    //4.2 sec

    @Override
    public Collection<BoolExpr> getExpressions() {
        return pairCollection;
    }


    public BoolExpr removeFixupVarsFromExpr2(Collection<Var> fixupVars) {
        if (isConflict()) return this;

        //only applies to imply and iff

        BoolExpr e2 = getExpr2();

        if (e2.isVar() || e2.isAnd()) {

            LinkedHashSet<BoolExpr> e2Expressions = new LinkedHashSet<BoolExpr>(e2.getExpressions());

            for (Var fixupVar : fixupVars) {
                if (e2Expressions.contains(fixupVar)) {
                    e2Expressions.remove(fixupVar);
                }
                if (e2Expressions.size() == 0) return TRUE;
            }

            if (e2Expressions.size() == 1) {
                return new Imp(getExpr1(), e2Expressions.iterator().next());
            } else if (e2Expressions.size() > 1) {
                return new Imp(getExpr1(), new And(e2Expressions));
            } else {
                throw new IllegalStateException();
            }

        } else {
            return this;
        }


    }

    @Override
    public BoolExpr copy() {
        BoolExpr e1 = getExpr1();
        BoolExpr e2 = getExpr2();

        BoolExpr c1 = e1.copy();
        BoolExpr c2 = e2.copy();

        return create(getClass(), c1, c2);
    }

}

