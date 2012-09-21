package c3i.core.featureModel.shared;

import c3i.core.featureModel.shared.boolExpr.Var;

public interface EvalContext {

    Tri getValue(Var var);

}
