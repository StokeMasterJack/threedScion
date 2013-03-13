package c3i.featureModel.shared.search.decision;

import c3i.featureModel.shared.boolExpr.Var;

public abstract class SimpleAssignment implements Decision {

    protected final Var var;

    public SimpleAssignment(Var var) {
        this.var = var;
    }


}
