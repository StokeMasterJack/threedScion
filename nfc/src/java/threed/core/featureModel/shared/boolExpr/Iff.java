package threed.core.featureModel.shared.boolExpr;

import threed.core.featureModel.shared.AutoAssignContext;
import threed.core.featureModel.shared.Bit;
import threed.core.featureModel.shared.EvalContext;
import threed.core.featureModel.shared.Tri;

/**
 * AKA bi-implication
 * same as (F => G) && (G => F)
 */
public class Iff extends Pair {

    public static final Type TYPE = Type.Iff;

    private final int hash;

    public Iff(BoolExpr expr1, BoolExpr expr2) {
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
        return "==";
    }


    public And toConjunctionOfImplications() {
        Imp imp1 = imp(getExpr1(), getExpr2());
        Imp imp2 = imp(getExpr2(), getExpr1());
        return and(imp1, imp2);
    }

    public Or toOrOrAnds() {

        BoolExpr e1 = getExpr1();
        BoolExpr e2 = getExpr2();

        And bothTrue = and(e1, e2);
        And bothFalse = and(not(e1), not(e2));

        return or(bothTrue, bothFalse);

    }


    /**
     * 0 0 1
     * 0 1 0
     * 1 0 0
     * 1 1 1
     */
    @Override
    public void autoAssignTrue(AutoAssignContext ctx, int depth) {

        logAutoAssignTrue(depth);

        BoolExpr e1 = getExpr1();
        BoolExpr e2 = getExpr2();


        BoolExpr v1 = e1.simplify(ctx);
        BoolExpr v2 = e2.simplify(ctx);

        if (v1.isFalse() && v2.isFalse()) {
            //ignore;
        } else if (v1.isTrue() && v2.isTrue()) {
            //ignore
        } else if (v1.isFalse() && v2.isTrue()) {
            throw new IffAutoAssignTrueException(this, false, true,ctx);

        } else if (v1.isTrue() && v2.isFalse()) {

            if (BoolExpr.debugMode) {

                System.err.println("---------------------------");
                System.err.println("iif constraint failed:");
                System.err.println("\t" + e1 + " evaluates to " + e1.toString(ctx) + " which evaluates to true");
                System.err.println("\t" + e2 + " evaluates to " + e2.toString(ctx) + " which evaluates to false");
                System.err.println("which violates rules: ");
                System.err.println("\t" + this.toString());
                System.err.println("\t" + this.toString(ctx));
                System.err.println("============================");

            }

            throw new IffAutoAssignTrueException(this, true, false,ctx);
        } else if (v1.isTrue() && v2.isOpen()) {
            v2.autoAssignTrue(ctx, depth + 1);
        } else if (v1.isFalse() && v2.isOpen()) {
            v2.autoAssignFalse(ctx, depth + 1);
        } else if (v2.isTrue() && v1.isOpen()) {
            v1.autoAssignTrue(ctx, depth + 1);
        } else if (v2.isFalse() && v1.isOpen()) {
            v1.autoAssignFalse(ctx, depth + 1);
        }


    }

    @Override
    public void autoAssignFalse(AutoAssignContext ctx, int depth) {
        throw new UnsupportedOperationException("Iff.autoAssignFalse[" + ctx + "]");
    }


    @Override
    public Tri eval(EvalContext ctx) {

        Tri val1 = getExpr1().eval(ctx);
        Tri val2 = getExpr2().eval(ctx);

        if (val1.isFalse() && val2.isFalse()) return Bit.TRUE;
        else if (val1.isTrue() && val2.isTrue()) return Bit.TRUE;
        else if (val1.isFalse() && val2.isTrue()) return Bit.FALSE;
        else if (val1.isTrue() && val2.isFalse()) return Bit.FALSE;
        else return Bit.OPEN;

    }


    @Override
    public BoolExpr simplify(AutoAssignContext ctx) {
//        if (true) return this;
//        System.out.println("Iff.simplify");

        BoolExpr sExpr1 = getExpr1().simplify(ctx);
        BoolExpr sExpr2 = getExpr2().simplify(ctx);

        if (sExpr1.isFalse() && sExpr2.isFalse()) return TRUE;
        else if (sExpr1.isTrue() && sExpr2.isTrue()) return TRUE;
        else if (sExpr1.isFalse() && sExpr2.isTrue()) return FALSE;
        else if (sExpr1.isTrue() && sExpr2.isFalse()) return FALSE;

        else if (sExpr1.isTrue() && sExpr2.isOpen()) return sExpr2;
        else if (sExpr1.isOpen() && sExpr2.isTrue()) return sExpr1;

        else if (sExpr1.isFalse() && sExpr2.isOpen()) return not(sExpr2);
        else if (sExpr1.isOpen() && sExpr2.isFalse()) return not(sExpr1);


        else if (sExpr1 == getExpr1() && sExpr2 == getExpr2()) {
            return this;
        } else {
            return new Iff(sExpr1, sExpr2);
        }

    }

    /**
     * each equality constraint x = y by the clauses x ∨ ¬y, ¬x ∨ y,
     *
     * @param ctx
     * @return
     */

    @Override
    public BoolExpr toCnf(AutoAssignContext ctx) {

        BoolExpr simple = simplify(ctx);
        if (simple != this) return simple.toCnf(ctx);

        BoolExpr e1 = getExpr1();
        BoolExpr e2 = getExpr2();

        BoolExpr v1 = e1.toCnf(ctx);
        BoolExpr v2 = e2.toCnf(ctx);

        if (v1 != e1 || v2 != e2) {
            return new Iff(v1, v2).toCnf(ctx);
        }

        Or or1 = or(v1, not(v2));
        Or or2 = or(not(v1), v2);

        return and(or1, or2).toCnf(ctx);
//        return toOrOrAnds().toCnf(ctx);
    }


}