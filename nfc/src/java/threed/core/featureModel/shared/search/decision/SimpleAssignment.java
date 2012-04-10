package threed.core.featureModel.shared.search.decision;

import threed.core.featureModel.shared.boolExpr.Var;

public abstract class SimpleAssignment implements Decision {

    protected final Var var;

    public SimpleAssignment(Var var) {
        this.var = var;
    }


}
