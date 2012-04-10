package threed.core.featureModel.shared.boolExpr;

import threed.core.featureModel.shared.AutoAssignContext;

abstract public class AndAutoAssignFalseException extends AssignmentException {

    public AndAutoAssignFalseException(And expr, AutoAssignContext context) {
        super(expr, false, context);
    }

}
