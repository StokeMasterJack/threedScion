package c3i.core.featureModel.shared.boolExpr;

import c3i.core.featureModel.shared.AutoAssignContext;

public class FoundTrueTermOrAutoAssignFalseExceptionImpl extends OrAutoAssignFalseException {
    public FoundTrueTermOrAutoAssignFalseExceptionImpl(Or expr,AutoAssignContext context) {
        super(expr,context);
    }
}
