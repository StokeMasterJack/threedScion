package c3i.core.featureModel.shared.search.decision;

import c3i.core.featureModel.shared.AssignContext;
import c3i.core.featureModel.shared.boolExpr.ReassignmentException;
import c3i.core.featureModel.shared.boolExpr.Var;

public class FalseDecision extends SimpleAssignment {

    public FalseDecision(Var var) {
        super(var);
    }

    public void makeAssignment(AssignContext ctx) throws ReassignmentException {
        ctx.assignFalse(var);
    }

    @Override
    public String toString() {
        return var + "[F]";
    }

}
