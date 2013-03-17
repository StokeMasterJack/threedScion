package c3i.featureModel.shared.picks;

import c3i.featureModel.shared.boolExpr.Var;
import c3i.featureModel.shared.node.Csp;

public interface PicksContext {

    int getVarCount();

    Var getVarOrNull(String varCode);

    Csp getConstraint();

    Var getVar(int varIndex);

    Var getVar(String varCode);
}
