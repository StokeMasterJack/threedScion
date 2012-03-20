package com.tms.threed.threedCore.featureModel.shared.boolExpr;

abstract public class OrAutoAssignFalseException extends AssignmentException {

    public OrAutoAssignFalseException(Or expr) {
        super(expr, false);
    }

}
