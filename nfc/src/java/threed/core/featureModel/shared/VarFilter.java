package threed.core.featureModel.shared;

import threed.core.featureModel.shared.boolExpr.Var;

public interface VarFilter {
    boolean accept(Var var);
    int getOutputVarCount();

}
