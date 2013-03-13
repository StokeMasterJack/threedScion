package c3i.featureModel.shared;

import c3i.featureModel.shared.boolExpr.Var;

public interface EvalContext {

    Tri getValue(Var var);

}
