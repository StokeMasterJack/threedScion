package c3i.core.featureModel.shared;

import c3i.core.featureModel.shared.boolExpr.Var;

public interface VarFilter {
    boolean accept(Var var);
    int getOutputVarCount();

}
