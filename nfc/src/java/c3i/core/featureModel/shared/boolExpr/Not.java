package c3i.core.featureModel.shared.boolExpr;

import c3i.core.featureModel.shared.AutoAssignContext;
import c3i.core.featureModel.shared.Bit;
import c3i.core.featureModel.shared.EvalContext;
import c3i.core.featureModel.shared.Tri;

import java.util.Collection;


public class Not extends Unary {

    public static final Type TYPE = Type.Not;

    private final int hash;

    public Not(BoolExpr expr) {
        super(expr);
        this.hash = 31 * TYPE.id + expr.hashCode();
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
    public void autoAssignTrue(AutoAssignContext ctx,int depth) {
        logAutoAssignTrue(depth);
        getExpr().autoAssignFalse(ctx,depth);
    }

    @Override
    public void autoAssignFalse(AutoAssignContext ctx,int depth) {
        logAutoAssignFalse(depth);
        getExpr().autoAssignTrue(ctx,depth);
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
}

