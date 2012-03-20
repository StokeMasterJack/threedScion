package com.tms.threed.threedCore.featureModel.shared.boolExpr;

public class ImpAutoAssignTrueException extends AssignmentException {

    public ImpAutoAssignTrueException(Imp expr) {
        super(expr, true);
    }

}
