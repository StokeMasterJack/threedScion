package threed.core.featureModel.shared;

import threed.core.featureModel.shared.boolExpr.BoolExpr;
import threed.core.featureModel.shared.boolExpr.Var;

public interface IFeatureModel {

    BoolExpr getConstraint();

    Var getVarOrNull(String code);

    Var getVar(int i);

    Var getVar(String code);

}
