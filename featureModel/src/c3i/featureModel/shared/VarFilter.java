package c3i.featureModel.shared;

import c3i.featureModel.shared.boolExpr.Var;

public interface VarFilter {
    boolean accept(Var var);

    int getOutputVarCount();

}
