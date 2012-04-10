package threed.core.featureModel.shared.boolExpr;

import threed.core.featureModel.shared.AutoAssignContext;

public class AllTermsAreFalseXorAssignmentException extends XorAssignmentException {

    public AllTermsAreFalseXorAssignmentException(BoolExpr expr, AutoAssignContext context) {
        super(expr, context);
    }

}
