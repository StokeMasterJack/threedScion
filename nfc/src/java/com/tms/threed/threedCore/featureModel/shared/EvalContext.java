package com.tms.threed.threedCore.featureModel.shared;

import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;

public interface EvalContext {

    Tri getValue(Var var);

}
