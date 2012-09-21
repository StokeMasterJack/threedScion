package c3i.core.featureModel.shared.boolExpr;

import c3i.core.featureModel.shared.AutoAssignContext;

public class IffAutoAssignTrueException extends AssignmentException {

    private final Iff expr;
    private final boolean v1;
    private final boolean v2;

    public IffAutoAssignTrueException(Iff expr, boolean v1, boolean v2,AutoAssignContext context) {
        super(expr, true,context); //iff only supports autoAssignTrue
        assert v1 != v2;

        this.expr = expr;
        this.v1 = v1;
        this.v2 = v2;
    }

    @Override
    public String getMessage() {
        return "The following IFF expression evaluated to false when true was expected: " + expr.toString() + ". More specifically, " + expr.getExpr1() + " evaluated " + v1 + " and " + expr.getExpr2() + " evaluated " + v2 + "";
    }

    @Override
    public String getMessage(AutoAssignContext ctx) {
        BoolExpr e1 = expr.getExpr1();
        BoolExpr e2 = expr.getExpr2();
        return "The following IFF expression evaluated to false when true was expected: " + expr.toString() + " which evaluated to [" + expr.toString(ctx) + "]. More specifically, " + e1 + " evaluated to " + e1.toString(ctx) + " which evaluated to " + v1 + " and " + e2 + " evaluated to " + e2.toString(ctx) + " which evaluated to " + v2 + "";
    }

    public boolean getV1() {
        return v1;
    }

    public boolean getV2() {
        return v2;
    }

}
