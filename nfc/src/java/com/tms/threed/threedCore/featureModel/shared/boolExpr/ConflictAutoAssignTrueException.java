package com.tms.threed.threedCore.featureModel.shared.boolExpr;

import com.tms.threed.threedCore.featureModel.shared.AutoAssignContext;

public class ConflictAutoAssignTrueException extends AssignmentException {

    public ConflictAutoAssignTrueException(Conflict expr,AutoAssignContext context) {
        super(expr, true,context);
    }

}
