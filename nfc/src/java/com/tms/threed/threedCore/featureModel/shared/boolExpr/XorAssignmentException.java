package com.tms.threed.threedCore.featureModel.shared.boolExpr;

import com.tms.threed.threedCore.featureModel.shared.AutoAssignContext;

abstract public class XorAssignmentException extends AssignmentException {

    public XorAssignmentException(BoolExpr expr, AutoAssignContext context) {
        super(expr, true, context); //Xor only support autoAssignTrue
    }
}
