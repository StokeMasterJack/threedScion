package c3i.featureModel.shared.boolExpr;

import c3i.featureModel.shared.AutoAssignContext;

public class FoundTrueTermOrAutoAssignFalseExceptionImpl extends OrAutoAssignFalseException {
    public FoundTrueTermOrAutoAssignFalseExceptionImpl(Or expr, AutoAssignContext context) {
        super(expr, context);
    }
}
