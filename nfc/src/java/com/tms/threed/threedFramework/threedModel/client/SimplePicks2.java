package com.tms.threed.threedFramework.threedModel.client;

import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedFramework.imageModel.shared.slice.SimplePicks;

public interface SimplePicks2 extends SimplePicks {

    boolean isPicked(Var var);

    boolean isInvalidBuild();

    boolean isValidBuild();

    String getErrorMessage();


}
