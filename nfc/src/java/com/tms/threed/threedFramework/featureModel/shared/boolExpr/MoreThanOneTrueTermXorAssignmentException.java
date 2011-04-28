package com.tms.threed.threedFramework.featureModel.shared.boolExpr;

public class MoreThanOneTrueTermXorAssignmentException extends XorAssignmentException {

    public MoreThanOneTrueTermXorAssignmentException(BoolExpr expr) {
        super(expr);
    }

}
