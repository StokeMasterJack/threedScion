package c3i.featureModel.shared.boolExpr;

import c3i.featureModel.shared.AutoAssignContext;

abstract public class XorAssignmentException extends AssignmentException {

    public XorAssignmentException(BoolExpr expr, AutoAssignContext context) {
        super(expr, true, context); //Xor only support autoAssignTrue
    }
}
