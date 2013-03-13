package c3i.featureModel.shared.search.decision;

import c3i.featureModel.shared.AssignContext;
import c3i.featureModel.shared.boolExpr.ReassignmentException;
import c3i.featureModel.shared.boolExpr.Var;

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
