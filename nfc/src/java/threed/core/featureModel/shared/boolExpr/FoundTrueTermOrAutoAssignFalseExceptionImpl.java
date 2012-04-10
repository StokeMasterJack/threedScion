package threed.core.featureModel.shared.boolExpr;

import threed.core.featureModel.shared.AutoAssignContext;

public class FoundTrueTermOrAutoAssignFalseExceptionImpl extends OrAutoAssignFalseException {
    public FoundTrueTermOrAutoAssignFalseExceptionImpl(Or expr,AutoAssignContext context) {
        super(expr,context);
    }
}
