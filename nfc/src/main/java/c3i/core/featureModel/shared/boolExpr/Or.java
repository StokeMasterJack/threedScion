package c3i.core.featureModel.shared.boolExpr;

import c3i.core.featureModel.shared.AutoAssignContext;
import c3i.core.featureModel.shared.Bit;
import c3i.core.featureModel.shared.EvalContext;
import c3i.core.featureModel.shared.Tri;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class Or extends Junction {

    public static final Type TYPE = Type.Or;

    private static final String ALL_TERMS_EVALUATE_TO_FALSE = "All terms evaluate to false";
    private static final String A_CHILD_EXPRESSION_RETURNED_TRUE = "A child Expression returned true";

    private final int hash;

    public Or(LinkedHashSet<BoolExpr> expressions) {
        super(expressions);
        assert expressions.size() >= 2;
        this.hash = 31 * TYPE.id + expressions.hashCode();
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
        return "OR";
    }

    @Override
    public Tri eval(EvalContext ctx) {

        if (expressions.size() < 2) throw new IllegalStateException();

        int falseCount = 0;

        for (BoolExpr expr : expressions) {
            Tri val = expr.eval(ctx);
            if (val.isTrue()) {
                return Bit.TRUE;
            } else if (val.isFalse()) {
                falseCount++;
            } else if (val.isOpen()) {
                //open
            } else {
                throw new IllegalStateException();
            }
        }


        boolean allFalse = falseCount == expressions.size();

        if (allFalse) {
            return Bit.FALSE;
        } else {
            return Bit.OPEN;
        }

    }


    @Override
    public BoolExpr simplify(AutoAssignContext ctx) {
//        if (true) return this;
//        System.out.println("Or.simplify");

        int L = getExprCount();
        assert L > 1;

        boolean anyChange = false;

        Expressions a = new Expressions();

        for (BoolExpr e : getExpressions()) {

            BoolExpr v = e.simplify(ctx);

            if (v != e) {
                anyChange = true;
            }

            if (v.isTrue()) {
                return TRUE;
            } else if (v.isFalse()) {
                //skip it
                anyChange = true;
            } else if (v.isOr()) {
                anyChange = true;
                for (BoolExpr ee : v.getExpressions()) {
                    BoolExpr vv = ee.simplify(ctx);
                    if (vv.isTrue()) {
                        return TRUE;
                    } else if (vv.isFalse()) {
                        //skip it
                        anyChange = true;
                    } else if (vv.isOpen()) {
                        try {
                            a.add(vv);
                        } catch (NegatingVarsException e1) {
                            return TRUE;
                        }
                    } else {
                        throw new IllegalStateException();
                    }
                }
            } else if (v.isOpen()) {
                try {
                    a.add(v);
                } catch (NegatingVarsException e1) {
                    return TRUE;
                }
            } else {
                throw new IllegalStateException();
            }
        }

        if (a.size() != expressions.size()) {
            anyChange = true;
        }

        if (!anyChange) return this;

        if (a.size() == 1) {
            return a.getFirst();
        } else if (a.isEmpty()) {
            return FALSE;
        } else {
            return new Or(a);
        }

    }

    @Override
    public void autoAssignTrue(AutoAssignContext ctx, int depth) {
        logAutoAssignTrue(depth);

        int falseCount = 0;
        int trueCount = 0;
        int openCount = 0;

        BoolExpr firstOpen = null;

        for (BoolExpr expr : expressions) {
            BoolExpr v = expr.simplify(ctx);

            if (v.isOpen()) {
                firstOpen = v;
                openCount++;
            } else if (v.isFalse()) {
                falseCount++;
            } else { //true
                trueCount++;
            }
        }

        assert falseCount + trueCount + openCount == expressions.size();

        if (falseCount == expressions.size()) {
            throw new AllTermsFalseOrAutoAssignTrueException(this,ctx);
        }

        if (openCount == 1 && trueCount == 0) {
            //i.e. all false and one open
            firstOpen.autoAssignTrue(ctx, depth + 1);
        }

    }

    @Override
    public void autoAssignFalse(AutoAssignContext ctx, int depth) {
        logAutoAssignFalse(depth);
        for (BoolExpr expr : expressions) {
            Tri value = expr.eval(ctx);
            if (value.isTrue()) {
                throw new FoundTrueTermOrAutoAssignFalseExceptionImpl(this,ctx);
            } else if (value.isFalse()) {
                //cool;
            } else { //open
                expr.autoAssignFalse(ctx, depth + 1);
            }
        }
    }

    @Override
    public BoolExpr toCnf(AutoAssignContext ctx) {

        BoolExpr simple = simplify(ctx);
        if (simple != this) {
            return simple.toCnf(ctx);
        }

        boolean anyChange = false;

        Expressions a = new Expressions();
        for (BoolExpr expr : getExpressions()) {
            BoolExpr sExpr = expr.toCnf(ctx);
            if (sExpr != expr) {
                anyChange = true;
            }
            a.add(sExpr);
        }

        if (anyChange) {
            return new Or(a).toCnf(ctx);
        } else {

            //a or (b and c and d) = (a or b) and (a or c) and (a or d)
            And firstAnd = getFirstAnd(expressions);
            if (firstAnd != null && expressions.size() < 4) {
                Expressions orTerms = new Expressions();
                for (BoolExpr f : expressions) {
                    if (f != firstAnd) {
                        orTerms.add(f);
                    }
                }

                BoolExpr x;
                if (orTerms.size() == 0) {
                    throw new IllegalStateException();
                } else if (orTerms.size() == 1) {
                    x = orTerms.getFirst();
                } else {
                    x = new Or(orTerms);
                }
                Expressions newAndTerms = new Expressions();
                for (BoolExpr faExpr : firstAnd.getExpressions()) {
                    Or or = or(x, faExpr);
                    newAndTerms.add(or);
                }

                return new And(newAndTerms).toCnf(ctx);
            } else {
                return this;
            }

        }

    }


    static And getFirstAnd(Collection<BoolExpr> c) {
        for (BoolExpr e : c) {
            if (e.isAnd()) return e.asAnd();
        }
        return null;
    }

    @Override
    public boolean containsPngVar() {
        if (!isClause()) throw new IllegalStateException();
        for (BoolExpr e : expressions) {
            if (e.isPngVar()) return true;
        }
        return false;
    }

    @Override
    public boolean isClause() {
        for (BoolExpr e : expressions) {
            if (!e.isLiteral()) return false;
        }
        return true;
    }

    public boolean isImplyish() {
        if (expressions.size() != 2) return false;

        Iterator<BoolExpr> it = expressions.iterator();
        BoolExpr e1 = it.next();
        BoolExpr e2 = it.next();

        return e1.isNot() || e2.isNot();
    }

    public Imp asImp() {
        if (expressions.size() != 2) return null;

        Iterator<BoolExpr> it = expressions.iterator();
        BoolExpr e1 = it.next();
        BoolExpr e2 = it.next();

        if (e1.isNot()) {
            return new Imp(e1, e2);
        } else if (e2.isNot()) {
            return new Imp(e2, e1);
        } else {
            return null;
        }
    }


}