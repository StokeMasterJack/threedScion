package com.tms.threed.threedFramework.featureModel.shared.boolExpr;

abstract public class AndAutoAssignFalseException extends AssignmentException{

    public AndAutoAssignFalseException(And expr) {
        super(expr, false);
    }

}
