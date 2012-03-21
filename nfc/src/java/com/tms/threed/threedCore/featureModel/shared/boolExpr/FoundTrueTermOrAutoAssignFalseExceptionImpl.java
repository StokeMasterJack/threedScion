package com.tms.threed.threedCore.featureModel.shared.boolExpr;

import com.tms.threed.threedCore.featureModel.shared.AutoAssignContext;

public class FoundTrueTermOrAutoAssignFalseExceptionImpl extends OrAutoAssignFalseException {
    public FoundTrueTermOrAutoAssignFalseExceptionImpl(Or expr,AutoAssignContext context) {
        super(expr,context);
    }
}
