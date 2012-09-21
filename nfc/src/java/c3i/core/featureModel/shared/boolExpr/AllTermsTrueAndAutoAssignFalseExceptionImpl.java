package c3i.core.featureModel.shared.boolExpr;

import c3i.core.featureModel.shared.AutoAssignContext;

public class AllTermsTrueAndAutoAssignFalseExceptionImpl extends AndAutoAssignFalseException {

    public AllTermsTrueAndAutoAssignFalseExceptionImpl(And expr,AutoAssignContext context) {
        super(expr,context);
    }
}
