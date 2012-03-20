package com.tms.threed.threedCore.featureModel.shared.boolExpr;

public class FoundTrueTermOrAutoAssignFalseExceptionImpl extends OrAutoAssignFalseException {
    public FoundTrueTermOrAutoAssignFalseExceptionImpl(Or expr) {
        super(expr);
    }
}
