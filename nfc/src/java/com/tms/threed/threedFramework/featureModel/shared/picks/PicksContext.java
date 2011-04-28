package com.tms.threed.threedFramework.featureModel.shared.picks;

import com.tms.threed.threedFramework.featureModel.shared.boolExpr.MasterConstraint;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;

public interface PicksContext {

    int getVarCount();

    Var getVarOrNull(String varCode);

    MasterConstraint getConstraint();

    Var getVar(int varIndex);

    Var getVar(String varCode);
}
