package c3i.core.featureModel.shared.boolExpr;

import c3i.core.featureModel.shared.AutoAssignContext;

public class ReassignmentException extends AssignmentException {

    /**
     * @param expr  the expression on which autoAssign was called
     * @param value the autoAssign value.
     *              If true, then autoAssignTrue was called
     *              If false, then autoAssignFalse was called
     */
    public ReassignmentException(BoolExpr expr, boolean value, AutoAssignContext context) {
        super(expr, value, context);
    }

}
