package c3i.featureModel.shared.search.decision;

import c3i.featureModel.shared.AssignContext;
import c3i.featureModel.shared.boolExpr.ReassignmentException;
import c3i.featureModel.shared.boolExpr.Var;

public class TrueDecision extends SimpleAssignment {

    public TrueDecision(Var var) {
        super(var);
    }

    public void makeAssignment(AssignContext ctx) throws ReassignmentException {
        ctx.assignTrue(var);
    }

    @Override
    public String toString() {
        return var + "[T]";
    }


}
