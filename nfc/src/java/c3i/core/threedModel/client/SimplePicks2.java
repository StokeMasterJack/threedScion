package c3i.core.threedModel.client;

import c3i.core.featureModel.shared.boolExpr.AssignmentException;
import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.core.imageModel.shared.SimplePicks;

public interface SimplePicks2 extends SimplePicks {

    boolean isPicked(Object var);

    boolean isInvalidBuild();

    boolean isValidBuild();

    AssignmentException getException();

    String getErrorMessage();
}
