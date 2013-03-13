package c3i.featureModel.shared;

import c3i.featureModel.shared.boolExpr.BoolExpr;
import c3i.featureModel.shared.boolExpr.Var;

public interface IFeatureModel {

    BoolExpr getConstraint();

    Var getVarOrNull(String code);

    Var getVar(int i);

    Var getVar(String code);

}
