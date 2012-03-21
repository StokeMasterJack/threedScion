package com.tms.threed.threedCore.threedModel.client;

import com.tms.threed.threedCore.featureModel.shared.boolExpr.AssignmentException;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedCore.imageModel.shared.slice.SimplePicks;

public interface SimplePicks2 extends SimplePicks {

    boolean isPicked(Var var);

    boolean isInvalidBuild();

    boolean isValidBuild();

    AssignmentException getException();

    String getErrorMessage();
}
