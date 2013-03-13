package c3i.featureModel.shared.picks;

import c3i.featureModel.shared.boolExpr.MasterConstraint;
import c3i.featureModel.shared.boolExpr.Var;

public interface PicksContext {

    int getVarCount();

    Var getVarOrNull(String varCode);

    MasterConstraint getConstraint();

    Var getVar(int varIndex);

    Var getVar(String varCode);
}
