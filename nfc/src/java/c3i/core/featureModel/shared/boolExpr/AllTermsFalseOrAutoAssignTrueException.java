package c3i.core.featureModel.shared.boolExpr;

import c3i.core.featureModel.shared.AutoAssignContext;

public class AllTermsFalseOrAutoAssignTrueException extends OrAutoAssignTrueException {
    public AllTermsFalseOrAutoAssignTrueException(Or expr, AutoAssignContext context) {
        super(expr, context);
    }
}
