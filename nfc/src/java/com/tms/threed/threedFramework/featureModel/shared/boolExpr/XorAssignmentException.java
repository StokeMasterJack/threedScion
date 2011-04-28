package com.tms.threed.threedFramework.featureModel.shared.boolExpr;

abstract public class XorAssignmentException extends AssignmentException{

    public XorAssignmentException(BoolExpr expr) {
        super(expr, true); //Xor only support autoAssignTrue
    }
}
