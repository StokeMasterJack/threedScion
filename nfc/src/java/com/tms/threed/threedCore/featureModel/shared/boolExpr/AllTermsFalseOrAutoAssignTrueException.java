package com.tms.threed.threedCore.featureModel.shared.boolExpr;

import com.tms.threed.threedCore.featureModel.shared.AutoAssignContext;

public class AllTermsFalseOrAutoAssignTrueException extends OrAutoAssignTrueException {
    public AllTermsFalseOrAutoAssignTrueException(Or expr, AutoAssignContext context) {
        super(expr, context);
    }
}
