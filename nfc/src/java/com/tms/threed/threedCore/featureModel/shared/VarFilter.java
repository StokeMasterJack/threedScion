package com.tms.threed.threedCore.featureModel.shared;

import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;

public interface VarFilter {
    boolean accept(Var var);
    int getOutputVarCount();

}
