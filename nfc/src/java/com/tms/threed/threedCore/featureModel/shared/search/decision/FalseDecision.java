package com.tms.threed.threedCore.featureModel.shared.search.decision;

import com.tms.threed.threedCore.featureModel.shared.AssignContext;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.AssignmentException;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;

public class FalseDecision extends SimpleAssignment {

    public FalseDecision(Var var) {
        super(var);
    }

    public void makeAssignment(AssignContext ctx)  throws AssignmentException {
        ctx.assignFalse(var);
    }

    @Override
    public String toString() {
        return var + "[F]";
    }

}
