package com.tms.threed.threedFramework.featureModel.shared.boolExpr;

public class ImpAutoAssignTrueException extends AssignmentException {

    public ImpAutoAssignTrueException(Imp expr) {
        super(expr, true);
    }

}
