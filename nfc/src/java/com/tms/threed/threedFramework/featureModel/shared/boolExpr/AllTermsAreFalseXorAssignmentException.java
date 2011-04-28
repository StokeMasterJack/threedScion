package com.tms.threed.threedFramework.featureModel.shared.boolExpr;

public class AllTermsAreFalseXorAssignmentException extends XorAssignmentException {

    public AllTermsAreFalseXorAssignmentException(BoolExpr expr) {
        super(expr);
    }

}
