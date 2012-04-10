package threed.core.featureModel.shared.picks;

import threed.core.featureModel.shared.boolExpr.MasterConstraint;
import threed.core.featureModel.shared.boolExpr.Var;

public interface PicksContext {

    int getVarCount();

    Var getVarOrNull(String varCode);

    MasterConstraint getConstraint();

    Var getVar(int varIndex);

    Var getVar(String varCode);
}
