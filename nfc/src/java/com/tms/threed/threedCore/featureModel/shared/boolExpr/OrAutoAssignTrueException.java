package com.tms.threed.threedCore.featureModel.shared.boolExpr;

import com.tms.threed.threedCore.featureModel.shared.AutoAssignContext;

abstract public class OrAutoAssignTrueException extends AssignmentException {

    public OrAutoAssignTrueException(Or expr,AutoAssignContext context) {
        super(expr, true,context);
    }

}
