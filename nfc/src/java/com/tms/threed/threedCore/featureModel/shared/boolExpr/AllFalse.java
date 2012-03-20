package com.tms.threed.threedCore.featureModel.shared.boolExpr;

import com.tms.threed.threedCore.featureModel.shared.AutoAssignContext;
import com.tms.threed.threedCore.featureModel.shared.EvalContext;
import com.tms.threed.threedCore.featureModel.shared.Tri;

import java.util.LinkedHashSet;

public class AllFalse extends Junction {

    public static final Type TYPE = Type.AllFalse;

    private final int hash;

    AllFalse(LinkedHashSet<BoolExpr> expressions) {
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
        throw new UnsupportedOperationException();
    }


    @Override
    public void autoAssignTrue(AutoAssignContext ctx,int depth) throws AssignmentException {
        throw new UnsupportedOperationException();
    }


    @Override
    public BoolExpr simplify(AutoAssignContext ctx) {

        int L = getExprCount();
        assert L > 1;

        boolean anyChange = false;


        Expressions a = new Expressions();

        for (BoolExpr e : expressions) {
            BoolExpr v = e.simplify(ctx);
            if (v != e) {
                anyChange = true;
            }

            if (v.isTrue()) {
                return FALSE;
            } else if (v.isFalse()) {
                //skip it
            } else {
                try {
                    a.add(v);
                } catch (NegatingVarsException e1) {
                    return FALSE;
                }
            }


        }

        if (a.size() != expressions.size()) {
            anyChange = true;
        }

        if (!anyChange) return this;

        if (a.size() == 1) {
            return a.getFirst();
        } else if (a.isEmpty()) {
            return TRUE;
        } else {
            return new AllFalse(a);
        }


    }


    @Override
    public void autoAssignFalse(AutoAssignContext ctx,int depth) throws AssignmentException {
        throw new UnsupportedOperationException();
    }


    @Override
    public String getSymbol() {
        return "All-False";
    }


    @Override
    public BoolExpr toCnf(AutoAssignContext ctx) {
        throw new UnsupportedOperationException();
    }

    public boolean isCnf() {
        for (BoolExpr expr : expressions) {
            if (!expr.isClause()) {
                return false;
            }
        }
        return true;
    }


}
