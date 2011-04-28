package com.tms.threed.threedFramework.featureModel.shared.boolExpr;

public class AllTermsFalseOrAutoAssignTrueException extends OrAutoAssignTrueException {
    public AllTermsFalseOrAutoAssignTrueException(Or expr) {
        super(expr);
    }
}
