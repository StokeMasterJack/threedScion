package com.tms.threed.threedCore.featureModel.shared.boolExpr;

abstract public class OrAutoAssignTrueException extends AssignmentException {

    public OrAutoAssignTrueException(Or expr) {
        super(expr, true);
    }

}
