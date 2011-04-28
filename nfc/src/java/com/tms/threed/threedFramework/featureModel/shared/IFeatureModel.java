package com.tms.threed.threedFramework.featureModel.shared;

import com.tms.threed.threedFramework.featureModel.shared.boolExpr.BoolExpr;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;

public interface IFeatureModel {

    BoolExpr getConstraint();

    Var getVarOrNull(String code);

    Var getVar(int i);

    Var getVar(String code);

}
