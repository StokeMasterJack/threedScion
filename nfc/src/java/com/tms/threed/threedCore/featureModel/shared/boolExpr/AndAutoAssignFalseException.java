package com.tms.threed.threedCore.featureModel.shared.boolExpr;

abstract public class AndAutoAssignFalseException extends AssignmentException{

    public AndAutoAssignFalseException(And expr) {
        super(expr, false);
    }

}
