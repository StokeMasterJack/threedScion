package c3i.core.featureModel.shared.boolExpr;

import c3i.core.featureModel.shared.AutoAssignContext;

public class AllTermsAreFalseXorAssignmentException extends XorAssignmentException {

    public AllTermsAreFalseXorAssignmentException(BoolExpr expr, AutoAssignContext context) {
        super(expr, context);
    }

}
