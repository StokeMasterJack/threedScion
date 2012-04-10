package threed.core.featureModel.shared.boolExpr;

import threed.core.featureModel.shared.AutoAssignContext;

abstract public class XorAssignmentException extends AssignmentException {

    public XorAssignmentException(BoolExpr expr, AutoAssignContext context) {
        super(expr, true, context); //Xor only support autoAssignTrue
    }
}
