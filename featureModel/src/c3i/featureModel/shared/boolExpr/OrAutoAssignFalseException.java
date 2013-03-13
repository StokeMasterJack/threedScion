package c3i.featureModel.shared.boolExpr;

import c3i.featureModel.shared.AutoAssignContext;

abstract public class OrAutoAssignFalseException extends AssignmentException {

    public OrAutoAssignFalseException(Or expr, AutoAssignContext context) {
        super(expr, false, context);
    }

}
