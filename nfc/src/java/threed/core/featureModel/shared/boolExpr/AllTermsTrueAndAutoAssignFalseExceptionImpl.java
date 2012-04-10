package threed.core.featureModel.shared.boolExpr;

import threed.core.featureModel.shared.AutoAssignContext;

public class AllTermsTrueAndAutoAssignFalseExceptionImpl extends AndAutoAssignFalseException {

    public AllTermsTrueAndAutoAssignFalseExceptionImpl(And expr,AutoAssignContext context) {
        super(expr,context);
    }
}
