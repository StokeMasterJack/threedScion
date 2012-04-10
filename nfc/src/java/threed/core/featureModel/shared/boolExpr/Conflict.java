package threed.core.featureModel.shared.boolExpr;

import threed.core.featureModel.shared.AutoAssignContext;
import threed.core.featureModel.shared.Bit;
import threed.core.featureModel.shared.EvalContext;
import threed.core.featureModel.shared.Tri;

import java.util.LinkedHashSet;

public class Conflict extends Pair {

    public static final Type TYPE = Type.Conflict;

    private final int hash;

    Conflict(BoolExpr expr1, BoolExpr expr2) {
        super(new UnorderedPair(expr1, expr2));
        this.hash = 31 * TYPE.id + content.hashCode();
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
        return "conflicts";
    }



    public void autoAssignTrue(AutoAssignContext ctx,int depth) throws AssignmentException {
        logAutoAssignTrue(depth);
        BoolExpr e1 = getExpr1();
        BoolExpr e2 = getExpr2();

        Tri v1 = e1.eval(ctx);
        Tri v2 = e2.eval(ctx);

        if (v1.isTrue() && v2.isOpen()) {
            e2.autoAssignFalse(ctx,depth+1);
        } else if (v1.isOpen() && v2.isTrue()) {
            e1.autoAssignFalse(ctx,depth+1);
        } else if (v1.isTrue() && v2.isTrue()) {
            throw new ConflictAutoAssignTrueException(this,ctx);
        }

    }

    public void autoAssignFalse(AutoAssignContext ctx,int depth) throws AssignmentException {
        throw new UnsupportedOperationException("Conflict.autoAssignFalse[" + ctx + "]");
    }



    @Override
    public Tri eval(EvalContext ctx) {

        Tri val1 = getExpr1().eval(ctx);
        Tri val2 = getExpr2().eval(ctx);

        if (val1.isFalse() || val2.isFalse()) return Bit.TRUE;
        else if (val1.isTrue() && val2.isTrue()) return Bit.FALSE;
        else return Bit.OPEN;

    }


    @Override
    public BoolExpr simplify(AutoAssignContext ctx) {
//        if(true) return this;
//        System.out.println("Conflict.simplify");

        BoolExpr e1 = getExpr1();
        BoolExpr e2 = getExpr2();


        BoolExpr v1 = e1.simplify(ctx);
        BoolExpr v2 = e2.simplify(ctx);

        if (v1.isFalse() || v2.isFalse()) {
            return TRUE;
        } else if (v1.isTrue() && v2.isTrue()) {
            return FALSE;
        } else if (v1.isTrue() && v2.isNonConstant()) {
            return not(v2);
        } else if (v2.isTrue() && v1.isNonConstant()) {
            return not(v1);
        } else if (v1.isNonConstant() && v2.isNonConstant()) {
            if (v1 == e1 && v2 == e2) {
                return this;
            } else {
                return new Conflict(v1, v2);
            }
        } else {
            throw new IllegalStateException();
        }

    }


    public BoolExpr toCnf(AutoAssignContext ctx) {

        BoolExpr simple = simplify(ctx);
        if (simple != this) return simple.toCnf(ctx);

        BoolExpr e1 = getExpr1();
        BoolExpr e2 = getExpr2();

        BoolExpr v1 = e1.toCnf(ctx);
        BoolExpr v2 = e2.toCnf(ctx);

        if (v1 != e1 || v2 != e2) {
            return new Conflict(v1, v2).toCnf(ctx);
        }

        return this.toOrOfNots().toCnf(ctx);

    }

    public Or toOrOfNots() {
        return or(not(getExpr1()), not(getExpr2()));
    }

    public BoolExpr flatten() {
        if (getExpr2().isOr()) {
            Or r2Or = (Or) getExpr2();
            LinkedHashSet<BoolExpr> andTerms = new LinkedHashSet<BoolExpr>();
            for (BoolExpr e : r2Or.getExpressions()) {
                andTerms.add(new Conflict(getExpr1(), e));
            }
            return BoolExpr.and(andTerms);
        } else {
            return this;
        }
    }



}
