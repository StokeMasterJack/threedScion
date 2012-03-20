package com.tms.threed.threedCore.featureModel.shared.boolExpr;

public class MoreThanOneTrueTermXorAssignmentException extends XorAssignmentException {

    public MoreThanOneTrueTermXorAssignmentException(BoolExpr expr) {
        super(expr);
    }

}
