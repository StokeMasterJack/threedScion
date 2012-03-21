package com.tms.threed.threedCore.featureModel.shared.boolExpr;

import com.tms.threed.threedCore.featureModel.shared.AutoAssignContext;

public class AllTermsAreFalseXorAssignmentException extends XorAssignmentException {

    public AllTermsAreFalseXorAssignmentException(BoolExpr expr, AutoAssignContext context) {
        super(expr, context);
    }

}
