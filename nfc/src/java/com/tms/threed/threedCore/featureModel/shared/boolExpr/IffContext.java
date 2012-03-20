package com.tms.threed.threedCore.featureModel.shared.boolExpr;

public interface IffContext {

    boolean putSingleVarIff(Var var, BoolExpr exp);

    BoolExpr getReplacement(BoolExpr e);
}
