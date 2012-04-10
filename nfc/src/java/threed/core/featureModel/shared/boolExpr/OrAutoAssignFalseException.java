package threed.core.featureModel.shared.boolExpr;

import threed.core.featureModel.shared.AutoAssignContext;

abstract public class OrAutoAssignFalseException extends AssignmentException {

    public OrAutoAssignFalseException(Or expr,AutoAssignContext context) {
        super(expr, false,context);
    }

}
