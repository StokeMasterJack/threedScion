package c3i.featureModel.shared;

import c3i.featureModel.shared.boolExpr.Var;

public interface EvalContext {

    Tri getValue(Var var);

    boolean isAssigned(Var var);

    boolean isTrue(Var var);

    boolean isFalse(Var var);

    boolean isOpen(Var var);

}
