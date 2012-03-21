package com.tms.threed.threedCore.featureModel.shared.boolExpr;

import com.tms.threed.threedCore.featureModel.shared.AutoAssignContext;

abstract public class OrAutoAssignFalseException extends AssignmentException {

    public OrAutoAssignFalseException(Or expr,AutoAssignContext context) {
        super(expr, false,context);
    }

}
