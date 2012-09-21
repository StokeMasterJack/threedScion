package c3i.core.featureModel.shared;

import c3i.core.featureModel.shared.boolExpr.BoolExpr;
import c3i.core.featureModel.shared.boolExpr.Var;

public interface IFeatureModel {

    BoolExpr getConstraint();

    Var getVarOrNull(String code);

    Var getVar(int i);

    Var getVar(String code);

}
