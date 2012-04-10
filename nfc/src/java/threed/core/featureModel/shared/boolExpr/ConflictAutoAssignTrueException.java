package threed.core.featureModel.shared.boolExpr;

import threed.core.featureModel.shared.AutoAssignContext;

public class ConflictAutoAssignTrueException extends AssignmentException {

    public ConflictAutoAssignTrueException(Conflict expr,AutoAssignContext context) {
        super(expr, true,context);
    }

}
