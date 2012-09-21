package c3i.core.featureModel.shared.search.decision;

import c3i.core.featureModel.shared.boolExpr.Var;

public abstract class SimpleAssignment implements Decision {

    protected final Var var;

    public SimpleAssignment(Var var) {
        this.var = var;
    }


}
