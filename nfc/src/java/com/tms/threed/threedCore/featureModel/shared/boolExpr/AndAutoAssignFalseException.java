package com.tms.threed.threedCore.featureModel.shared.boolExpr;

import com.tms.threed.threedCore.featureModel.shared.AutoAssignContext;

abstract public class AndAutoAssignFalseException extends AssignmentException {

    public AndAutoAssignFalseException(And expr, AutoAssignContext context) {
        super(expr, false, context);
    }

}
