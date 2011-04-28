package com.tms.threed.threedFramework.featureModel.shared.boolExpr;

public class FoundTrueTermOrAutoAssignFalseExceptionImpl extends OrAutoAssignFalseException {
    public FoundTrueTermOrAutoAssignFalseExceptionImpl(Or expr) {
        super(expr);
    }
}
