package threed.core.featureModel.shared.search.decision;

import threed.core.featureModel.shared.AssignContext;
import threed.core.featureModel.shared.boolExpr.AssignmentException;
import threed.core.featureModel.shared.boolExpr.Var;

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
