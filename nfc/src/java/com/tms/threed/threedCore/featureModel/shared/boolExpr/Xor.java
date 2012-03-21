package com.tms.threed.threedCore.featureModel.shared.boolExpr;

import com.tms.threed.threedCore.featureModel.shared.AutoAssignContext;
import com.tms.threed.threedCore.featureModel.shared.Bit;
import com.tms.threed.threedCore.featureModel.shared.EvalContext;
import com.tms.threed.threedCore.featureModel.shared.Tri;

import java.util.LinkedHashSet;

/**
 * Must support a single child
 */
public class Xor extends Junction {

    public static final Type TYPE = Type.Xor;

    private final int hash;

    public Xor(LinkedHashSet<BoolExpr> expressions) {
        super(expressions);
        this.hash = 31 * TYPE.id + expressions.hashCode();
    }

    public Type getType() {
        return TYPE;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public BoolExpr toOrsAndConflicts() {
        if (expressions.size() == 0) {
            throw new IllegalStateException();
        } else if (expressions.size() == 1) {
            return getExpr();
        } else {
            Or orPart = or(expressions);

            LinkedHashSet<BoolExpr> conflicts = getConflicts();
            BoolExpr nandPart;
            if (conflicts.size() == 1) {
                nandPart = conflicts.iterator().next();
            } else {
                nandPart = and(conflicts);
            }


            return and(orPart, nandPart);
        }
    }

    @Override
    public String getSymbol() {
        return "XOR";
    }

    public BoolExpr toCnf(AutoAssignContext ctx) {

        BoolExpr simple = simplify(ctx);
        if (simple != this) return simple.toCnf(ctx);

        assert expressions.size() != 0;

        if (expressions.size() == 1) {
            return expressions.iterator().next().toCnf(ctx);
        } else {
            return toOrsAndConflicts().toCnf(ctx);
        }
    }

    public boolean isXorPickOneGroup() {
        assert expressions.size() != 0;

        Var parentVar = null;
        int i = 0;
        for (BoolExpr expr : expressions) {
            if (!expr.isVar()) return false;
            if (!expr.asVar().isRoot()) return false;
            if (!expr.asVar().isXorChild()) return false;

            Var p = expr.asVar().getParent();
            if (i == 0) {
                parentVar = p;
            } else {
                assert parentVar.equals(p);
            }
            i++;
        }
        return true;

    }

    @Override
    public Tri eval(EvalContext ctx) {

        int L = expressions.size();
        if (expressions.size() == 0) throw new IllegalStateException();


        int trueCount = 0;
        int falseCount = 0;
        int openCount = 0;

        for (BoolExpr expr : expressions) {
            Tri v = expr.eval(ctx);
            if (v.isTrue()) {
                trueCount++;
                if (trueCount > 1) return Bit.FALSE;
            } else if (v.isFalse()) {
                falseCount++;
            } else { //open
                openCount++;
            }
        }

        assert falseCount + trueCount + openCount == L;

        boolean allOpen = openCount == L;
        boolean allFalse = falseCount == L;
        boolean oneTrueRestFalse = trueCount == 1 && falseCount == (L - 1);


        if (allOpen) return Bit.OPEN;
        if (oneTrueRestFalse) return TRUE;

        if (trueCount > 1) {
            return FALSE;
        }
        if (allFalse) {
            return Bit.FALSE;
        }

        return Bit.OPEN;

    }

    @Override
    public BoolExpr simplify(AutoAssignContext ctx) {
//        if (true) return this;
//        System.out.println("Xor.simplify");

        boolean anyChange = false;

        for (BoolExpr e : expressions) {
            BoolExpr v = e.simplify(ctx);
            if (v != e) {
                anyChange = true;
                break;
            }
        }

        if (!anyChange) {
            return this;
        }


        int trueCount = 0;
        int falseCount = 0;

        LinkedHashSet<BoolExpr> opens = new LinkedHashSet<BoolExpr>();

        for (BoolExpr e : expressions) {
            BoolExpr v = e.simplify(ctx);
            if (v.isTrue()) {
                trueCount++;
                if (trueCount > 1) {
                    return FALSE;
                }
            } else if (v.isFalse()) {
                falseCount++;
            } else if (v.isOpen()) {
                opens.add(v);
            } else {
                throw new IllegalStateException();
            }
        }

        int L = expressions.size();


        int openCount = opens.size();

        assert falseCount + trueCount + openCount == L;

        boolean allOpen = openCount == L;
        boolean allFalse = falseCount == L;
        boolean oneTrueRestFalse = trueCount == 1 && falseCount == (L - 1);

//        if (openCount == 0) {
//            if (trueCount == 1) return TRUE;  //one true, rest false
//            else return FALSE;
//        } else if (openCount == 1) {
//            BoolExpr onlyUnassigned = opens.iterator().next();
//            if (trueCount == 0) return onlyUnassigned;
//            else if (trueCount == 1) return not(onlyUnassigned);
//            else throw new IllegalStateException();
//        } else if (openCount == 2) {
//            Iterator<BoolExpr> it = opens.iterator();
//            BoolExpr o1 = it.next();
//            BoolExpr o2 = it.next();
//            if (trueCount == 0) return new Conflict(o1, o2);
//            else if (trueCount == 1) return and(not(o1), not(o2));
//            else throw new IllegalStateException();
//        } else if (openCount > 2) {
//            if (trueCount == 0) return xor(opens);
//            else if (trueCount == 1) return not(or(opens));
//            else throw new IllegalStateException();
//        } else {
//            throw new IllegalStateException();
//        }


        if (trueCount == 0) {
            if (openCount == 0) { //all false
                assert allFalse;
                return FALSE;
            } else if (openCount == 1) { //all false - one open
                return opens.iterator().next();
            } else if (openCount > 1) {
                return new Xor(opens);
            } else {
                throw new IllegalStateException();
            }
        } else if (trueCount == 1) {
            if (openCount == 0) { //one true, rest false
                assert oneTrueRestFalse;
                return TRUE;
            } else if (openCount == 1) {
                return not(opens.iterator().next());
            } else {
                System.out.println(9999999);
                return new AllFalse(opens);
            }
        } else {
            throw new IllegalStateException();
        }


    }


    @Override
    public void autoAssignFalse(AutoAssignContext ctx, int depth) {
        throw new UnsupportedOperationException("Xor.autoAssignFalse[" + ctx + "]");
    }

    public void logAutoAssignTrue(int depth) {
        if (logAutoAssignments) {
            log(depth, "AutoAssign " + this + " true");
        }
    }

    public void logAutoAssignFalse(int depth) {
        if (logAutoAssignments) {
            log(depth, "AutoAssign " + this + " false");
            log(depth + 1, "Xor expressions: " + expressions);
        }
    }

    @Override
    public void autoAssignTrue(AutoAssignContext ctx, int depth) throws MoreThanOneTrueTermXorAssignmentException {
        logAutoAssignTrue(depth);
        int L = expressions.size();
        if (expressions.size() == 0) throw new IllegalStateException();

        XorTermsStates s = new XorTermsStates();

        int trueCount = 0;
        int falseCount = 0;

        LinkedHashSet<BoolExpr> a = new LinkedHashSet<BoolExpr>();

        for (BoolExpr expr : expressions) {
            Tri v = expr.simplify(ctx);
            if (v.isTrue()) {
                trueCount++;
                s.pushTrueTerm(expr);
                if (trueCount > 1) {
                    throw new MoreThanOneTrueTermXorAssignmentException(this, expr, s, ctx);
                }
            } else if (v.isFalse()) {
                s.pushFalseTerm(expr);
                falseCount++;
            } else { //open
                s.pushOpenTerm(expr);
                a.add(expr);
            }
        }

        assert falseCount == s.getFalseCount();
        assert a.size() == s.getOpenCount();

        int openCount = a.size();
        assert falseCount + trueCount + openCount == L;

        boolean allOpen = openCount == L;
        boolean allFalse = falseCount == L;
        boolean oneTrueRestFalse = trueCount == 1 && falseCount == (L - 1);


        if (trueCount == 0) {
            if (openCount == 0) { //all false
                assert allFalse;
                throw new AllTermsAreFalseXorAssignmentException(this, ctx);
            } else if (openCount == 1) { //all false - one open
                a.iterator().next().autoAssignTrue(ctx, depth + 1);
            } else if (openCount > 1) {

//                return new Xor(opens);
            } else {
                throw new IllegalStateException();
            }
        } else if (trueCount == 1) {
            if (openCount == 0) {
//                return TRUE;
            } else if (openCount == 1) {
                a.iterator().next().autoAssignFalse(ctx, depth + 1);
            } else {
                for (BoolExpr expr : a) {
                    expr.autoAssignFalse(ctx, depth + 1);
                }
            }
        } else {
            throw new IllegalStateException();
        }
    }


}