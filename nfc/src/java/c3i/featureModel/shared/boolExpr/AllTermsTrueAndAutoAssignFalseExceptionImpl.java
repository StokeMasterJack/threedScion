package c3i.featureModel.shared.boolExpr;

import c3i.featureModel.shared.AutoAssignContext;

public class AllTermsTrueAndAutoAssignFalseExceptionImpl extends AndAutoAssignFalseException {

    public AllTermsTrueAndAutoAssignFalseExceptionImpl(And expr, AutoAssignContext context) {
        super(expr, context);
    }
}
