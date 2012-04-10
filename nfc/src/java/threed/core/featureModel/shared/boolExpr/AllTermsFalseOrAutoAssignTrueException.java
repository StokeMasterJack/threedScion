package threed.core.featureModel.shared.boolExpr;

import threed.core.featureModel.shared.AutoAssignContext;

public class AllTermsFalseOrAutoAssignTrueException extends OrAutoAssignTrueException {
    public AllTermsFalseOrAutoAssignTrueException(Or expr, AutoAssignContext context) {
        super(expr, context);
    }
}
