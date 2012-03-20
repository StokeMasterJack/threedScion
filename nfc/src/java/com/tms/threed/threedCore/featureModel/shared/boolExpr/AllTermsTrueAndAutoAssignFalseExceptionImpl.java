package com.tms.threed.threedCore.featureModel.shared.boolExpr;

public class AllTermsTrueAndAutoAssignFalseExceptionImpl extends AndAutoAssignFalseException {

    public AllTermsTrueAndAutoAssignFalseExceptionImpl(And expr) {
        super(expr);
    }
}
