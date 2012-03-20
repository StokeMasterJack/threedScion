package com.tms.threed.threedCore.featureModel.shared.boolExpr;

public class ConflictAutoAssignTrueException extends AssignmentException {

    public ConflictAutoAssignTrueException(Conflict expr) {
        super(expr, true);
    }

}
