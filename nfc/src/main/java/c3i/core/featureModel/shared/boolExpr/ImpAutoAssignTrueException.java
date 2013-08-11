package c3i.core.featureModel.shared.boolExpr;

import c3i.core.featureModel.shared.AutoAssignContext;

public class ImpAutoAssignTrueException extends AssignmentException {

    public ImpAutoAssignTrueException(Imp expr,AutoAssignContext context) {
        super(expr, true,context);
    }

}
