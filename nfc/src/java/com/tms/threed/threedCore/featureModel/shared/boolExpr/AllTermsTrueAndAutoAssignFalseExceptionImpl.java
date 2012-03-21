package com.tms.threed.threedCore.featureModel.shared.boolExpr;

import com.tms.threed.threedCore.featureModel.shared.AutoAssignContext;

public class AllTermsTrueAndAutoAssignFalseExceptionImpl extends AndAutoAssignFalseException {

    public AllTermsTrueAndAutoAssignFalseExceptionImpl(And expr,AutoAssignContext context) {
        super(expr,context);
    }
}
