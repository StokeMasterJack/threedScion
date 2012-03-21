package com.tms.threed.threedCore.featureModel.shared.boolExpr;

import com.tms.threed.threedCore.featureModel.shared.AutoAssignContext;

public class ImpAutoAssignTrueException extends AssignmentException {

    public ImpAutoAssignTrueException(Imp expr,AutoAssignContext context) {
        super(expr, true,context);
    }

}
