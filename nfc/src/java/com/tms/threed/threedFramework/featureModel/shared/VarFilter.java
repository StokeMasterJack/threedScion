package com.tms.threed.threedFramework.featureModel.shared;

import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;

public interface VarFilter {
    boolean accept(Var var);
    int getOutputVarCount();

}
