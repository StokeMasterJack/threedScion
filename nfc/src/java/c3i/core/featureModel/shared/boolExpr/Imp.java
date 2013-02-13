package c3i.core.featureModel.shared.boolExpr;

import c3i.core.featureModel.shared.AutoAssignContext;
import c3i.core.featureModel.shared.Bit;
import c3i.core.featureModel.shared.EvalContext;
import c3i.core.featureModel.shared.Tri;

import java.util.LinkedHashSet;

public class Imp extends Pair {

    public static final Type TYPE = Type.Imp;


    private final int hash;

    Imp(BoolExpr expr1, BoolExpr expr2) {
        super(new OrderedPair(expr1, expr2));
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
        return "=>";
    }

    public BoolExpr toOrNotForm() {
        return or(BoolExpr.not(content.expr1), content.expr2);
    }

    /**
     * A B R
     * 0 0 1
     * 0 1 1
     * 1 1 1
     *
     * @param ctx
     * @return
     */
    public void autoAssignTrue(AutoAssignContext ctx, int depth) {
        logAutoAssignTrue(depth);
        BoolExpr e1 = content.expr1;
        BoolExpr e2 = content.expr2;

        Tri val1 = e1.eval(ctx);
        Tri val2 = e2.eval(ctx);

        if (val1.isTrue() && val2.isOpen()) {
            e2.autoAssignTrue(ctx, depth + 1);
        } else if (val2.isFalse() && val1.isOpen()) {

            if (BoolExpr.debugMode) {

                System.err.println("---------------------------");
                System.err.println("Implication.autoAssignTrue:");
                System.err.println("\t" + this.toString());
                System.err.println("\t" + this.toString(ctx));
                System.err.println("============================");


                System.err.println("\t since " + e2 + " evaluates to [" + e2.toString(ctx) + "] which evaluates to false");
                System.err.println("\t then  " + e1 + " which evaluates to [" + e1.toString(ctx) + "] must be autoAssigned to false also");


            }

            e1.autoAssignFalse(ctx, depth + 1);
        } else if (val1.isTrue() && val2.isFalse()) {
            throw new ImpAutoAssignTrueException(this, ctx);
        }
    }

    public void autoAssignFalse(AutoAssignContext ctx, int depth) {
        throw new UnsupportedOperationException();
    }


    /**
     * A B  R
     * 0 0  1
     * 0 1  1
     * 1 0  0
     * 1 1  1
     *
     * @param ctx
     * @return
     */
    @Override
    public Tri eval(EvalContext ctx) {

        BoolExpr e1 = content.expr1;
        BoolExpr e2 = content.expr2;

        Tri v1 = e1.eval(ctx);
        Tri v2 = e2.eval(ctx);

        if (v1.isFalse()) {
            return Bit.TRUE;
        } else if (v2.isTrue()) {
            return Bit.TRUE;
        } else if (v1.isTrue() && v2.isFalse()) {
            return Bit.FALSE;
        } else {
            return Bit.OPEN;
        }

    }


    @Override
    public BoolExpr simplify(AutoAssignContext ctx) {
//        System.out.println("Imp.simplify");

        BoolExpr e1 = content.expr1;
        BoolExpr e2 = content.expr2;

        BoolExpr v1 = e1.simplify(ctx);


        if (v1.isFalse()) {
            return TRUE;
        }

        BoolExpr v2 = e2.simplify(ctx);

        if (v1.isTrue()) {
            return v2;
        }

        if (v2.isTrue()) {
            return TRUE;
        }


        if (v2.isFalse()) {
            return not(v1);
        }
        if (v1 == e1 && v2 == e2) {
            return this;
        }

        return new Imp(v1, v2);
    }


    @Override
    public BoolExpr toCnf(AutoAssignContext ctx) {

        BoolExpr simple = simplify(ctx);
        if (simple != this) return simple.toCnf(ctx);

        if (isImplThatNeedsFlattening()) {
            return this.flatten().toCnf(ctx);
        }

        BoolExpr e1 = content.expr1;
        BoolExpr e2 = content.expr2;

        BoolExpr v1 = e1.toCnf(ctx);
        BoolExpr v2 = e2.toCnf(ctx);

        if (v1 != e1 || v2 != e2) {
            return new Imp(v1, v2).toCnf(ctx);
        }

        return toOrNotForm().toCnf(ctx);

    }

    public BoolExpr removeFixupVarFromExpr2(Var var) {
        BoolExpr e2 = getExpr2();
        if (e2.equals(var)) {
            return TRUE;
        } else if (e2.isAnd()) {
            And a = e2.asAnd();
            if (a.getExprCount() == 1 && a.getExpr().equals(var)) {
                return TRUE;
            } else {
                LinkedHashSet<BoolExpr> set = a.getExpressions();
                set.remove(var);
                return new Imp(getExpr1(), new And(set));
            }
        } else {
            throw new IllegalStateException();
        }


    }

    public BoolExpr flatten() {

        BoolExpr expr1 = content.expr1;
        BoolExpr expr2 = content.expr2;

        //flatten ands
        if (expr2 instanceof And) {
            LinkedHashSet<BoolExpr> set = new LinkedHashSet<BoolExpr>();
            for (BoolExpr ee : expr2.getExpressions()) {
                set.add(imp(expr1, ee));
            }
            return and(set);
        } else {
            return this;
        }
    }


    public BoolExpr convertImplicationToOr() {
        BoolExpr expr1 = content.expr1;
        BoolExpr expr2 = content.expr2;

        return or(not(expr1), expr2);
    }

    public Imp asImp() {
        return this;
    }

    public boolean isCompliment(BoolExpr other) {

        BoolExpr expr1 = content.expr1;
        BoolExpr expr2 = content.expr2;


        if (other.isImp()) {
            Imp that = other.asImp();
            return expr1.equals(that.content.expr2) && expr2.equals(that.content.expr1);
        } else if (other.isOr() && other.asOr().isImplyish()) {
            Imp that = other.asOr().asImp();
            return expr1.equals(that.content.expr2) && expr2.equals(that.content.expr1);
        } else {
            return false;
        }
    }

    public boolean isImplyish() {
        return true;
    }

    public boolean isImplThatNeedsFlattening() {
        return content.expr2.isAnd();
    }
}