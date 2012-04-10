package threed.core.featureModel.shared;

import threed.core.featureModel.shared.boolExpr.Var;

public interface EvalContext {

    Tri getValue(Var var);

}
