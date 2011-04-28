package com.tms.threed.threedFramework.featureModel.shared.boolExpr;

abstract public class OrAutoAssignFalseException extends AssignmentException {

    public OrAutoAssignFalseException(Or expr) {
        super(expr, false);
    }

}
