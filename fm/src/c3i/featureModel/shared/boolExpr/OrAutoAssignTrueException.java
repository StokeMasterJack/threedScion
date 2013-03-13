package c3i.featureModel.shared.boolExpr;

import c3i.featureModel.shared.AutoAssignContext;

abstract public class OrAutoAssignTrueException extends AssignmentException {

    public OrAutoAssignTrueException(Or expr, AutoAssignContext context) {
        super(expr, true, context);
    }

}
