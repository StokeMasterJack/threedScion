package com.tms.threed.threedFramework.featureModel.shared.boolExpr;

import com.tms.threed.threedFramework.featureModel.shared.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class And extends Junction {

    public static final Type TYPE = Type.And;

    private static final String FOUND_FALSE_IN_AND_S_TERM_LIST = "Found false in term list";
    private static final String ALL_AND_TERMS_ARE_ALREADY_TRUE = "All and terms are already true";

    private final int hash;

    public And(LinkedHashSet<BoolExpr> expressions) {
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
    public Tri eval(EvalContext ctx) {
        assert getExprCount() >= 2;

        if (expressions.size() == 1) {
            return getExpr().eval(ctx);
        }

        int trueCount = 0;
        for (BoolExpr e : expressions) {
            Tri v = e.eval(ctx);
            if (v.isFalse()) return Bit.FALSE;
            else if (v.isTrue()) trueCount++;
        }

        if (trueCount == expressions.size()) return Bit.TRUE;
        else return Bit.OPEN;
    }


    @Override
    public void autoAssignTrue(AutoAssignContext ctx, int depth) throws AssignmentException {
        logAutoAssignTrue(depth);
        for (BoolExpr e : expressions) {
            e.autoAssignTrue(ctx, depth + 1);
        }
    }


    @Override
    public BoolExpr simplify(AutoAssignContext ctx) {

        boolean anyChange = false;

        for (BoolExpr e : expressions) {
            BoolExpr v = e.simplify(ctx);
            if (e != v) {
                anyChange = true;
                break;
            }
        }

        if (!anyChange) return this;


        int L = getExprCount();
        assert L > 1;


        Expressions a = new Expressions();

        for (BoolExpr e : expressions) {
            BoolExpr v = e.simplify(ctx);
            if (v.isFalse()) {
                return FALSE;
            } else if (v.isTrue()) {
                //skip it
            } else {
                try {
                    a.add(v);
                } catch (NegatingVarsException e1) {
                    return FALSE;
                }
            }

        }


        if (a.size() == 1) {
            return a.getFirst();
        } else if (a.isEmpty()) {
            return TRUE;
        } else {
            return new And(a);
        }


    }

    @Override
    public void autoAssignFalse(AutoAssignContext ctx, int depth) throws AssignmentException {
        logAutoAssignFalse(depth);

        //at least one term must be false

        int L = getExprCount();

        int openCount = 0;

        BoolExpr firstOpen = null;

        for (BoolExpr e : getExpressions()) {
            BoolExpr v = e.simplify(ctx);
            if (v.isTrue()) {
                //skip
            } else if (v.isFalse()) {
                //we found our one false, condition satisfied, no point in further looping
                return;
            } else {  //open
                openCount++;
                firstOpen = v;
            }
        }


        boolean allTrue = openCount == 0;
        boolean oneOpenRestTrue = openCount == 1;

        if (allTrue) {
            throw new AllTermsTrueAndAutoAssignFalseExceptionImpl(this);
        }

        if (oneOpenRestTrue) {
            firstOpen.autoAssignFalse(ctx, depth + 1);
        }

    }


    @Override
    public String getSymbol
            () {
        return "AND";
    }


    @Override
    public List<Var> isConjunctionOfVars
            () {
        List<Var> vars = null;
        for (BoolExpr expr : expressions) {
            Var var = expr.asVar();
            if (var != null) {
                if (vars == null) vars = new ArrayList<Var>();
                vars.add(var);
            }
        }
        return vars;
    }


    @Override
    public BoolExpr toCnf(AutoAssignContext ctx) {
        BoolExpr simple = simplify(ctx);
        if (simple != this) return simple.toCnf(ctx);


        boolean anyChange = false;

        Expressions a = new Expressions();
        for (BoolExpr expr : getExpressions()) {

            BoolExpr sExpr = expr.toCnf(ctx);
            if (sExpr != expr) {
                anyChange = true;
            }

            a.add(sExpr);

        }

        if (!anyChange) return this;

        return new And(a);
    }

    public boolean isCnf() {
        for (BoolExpr expr : expressions) {
            if (!expr.isClause()) {
                return false;
            }
        }
        return true;
    }

    public BoolExpr findCompliment(BoolExpr expr) {
        Imp a = expr.asImp();
        if (a == null) return null;


        for (BoolExpr e : expressions) {
            Imp b = e.asImp();
            if (b == null) continue;

            if (a.getExpr1().equals(b.getExpr2()) && a.getExpr2().equals(b.getExpr1())) {
                return b;
            }

        }

        return null;
    }


    /**
     * Only applies to top-level constraint
     */
    private LinkedHashSet<BoolExpr> getConstraintsEffecting(Var var) {
        LinkedHashSet<BoolExpr> set = new LinkedHashSet<BoolExpr>();
        for (BoolExpr expr : getExpressions()) {
            if (expr.containsDeep(var)) set.add(expr);
        }
        return set;
    }


}
