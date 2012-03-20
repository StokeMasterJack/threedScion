package com.tms.threed.threedCore.featureModel.shared.boolExpr;

public class AllTermsAreFalseXorAssignmentException extends XorAssignmentException {

    public AllTermsAreFalseXorAssignmentException(BoolExpr expr) {
        super(expr);
    }

}
