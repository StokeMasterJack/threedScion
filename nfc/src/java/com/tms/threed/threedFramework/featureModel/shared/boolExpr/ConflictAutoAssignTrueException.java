package com.tms.threed.threedFramework.featureModel.shared.boolExpr;

public class ConflictAutoAssignTrueException extends AssignmentException {

    public ConflictAutoAssignTrueException(Conflict expr) {
        super(expr, true);
    }

}
