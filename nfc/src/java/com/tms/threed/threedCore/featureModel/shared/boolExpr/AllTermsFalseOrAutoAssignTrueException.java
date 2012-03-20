package com.tms.threed.threedCore.featureModel.shared.boolExpr;

public class AllTermsFalseOrAutoAssignTrueException extends OrAutoAssignTrueException {
    public AllTermsFalseOrAutoAssignTrueException(Or expr) {
        super(expr);
    }
}
