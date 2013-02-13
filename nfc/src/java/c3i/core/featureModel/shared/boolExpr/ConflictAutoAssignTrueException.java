package c3i.core.featureModel.shared.boolExpr;

import c3i.core.featureModel.shared.AutoAssignContext;

public class ConflictAutoAssignTrueException extends AssignmentException {

    public ConflictAutoAssignTrueException(Conflict expr, AutoAssignContext context) {
        super(expr, true, context);
    }

}
