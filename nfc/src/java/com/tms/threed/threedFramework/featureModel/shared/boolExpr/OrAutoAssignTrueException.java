package com.tms.threed.threedFramework.featureModel.shared.boolExpr;

abstract public class OrAutoAssignTrueException extends AssignmentException {

    public OrAutoAssignTrueException(Or expr) {
        super(expr, true);
    }

}
