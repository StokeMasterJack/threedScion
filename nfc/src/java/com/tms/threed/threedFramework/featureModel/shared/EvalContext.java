package com.tms.threed.threedFramework.featureModel.shared;

import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;

public interface EvalContext {

    Tri getValue(Var var);

}
