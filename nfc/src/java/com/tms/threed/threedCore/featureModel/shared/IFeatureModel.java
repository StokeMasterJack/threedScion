package com.tms.threed.threedCore.featureModel.shared;

import com.tms.threed.threedCore.featureModel.shared.boolExpr.BoolExpr;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;

public interface IFeatureModel {

    BoolExpr getConstraint();

    Var getVarOrNull(String code);

    Var getVar(int i);

    Var getVar(String code);

}
