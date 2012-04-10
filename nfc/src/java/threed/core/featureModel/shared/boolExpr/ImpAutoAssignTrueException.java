package threed.core.featureModel.shared.boolExpr;

import threed.core.featureModel.shared.AutoAssignContext;

public class ImpAutoAssignTrueException extends AssignmentException {

    public ImpAutoAssignTrueException(Imp expr,AutoAssignContext context) {
        super(expr, true,context);
    }

}
