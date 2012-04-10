package threed.core.threedModel.client;

import threed.core.featureModel.shared.boolExpr.AssignmentException;
import threed.core.featureModel.shared.boolExpr.Var;
import threed.core.imageModel.shared.slice.SimplePicks;

public interface SimplePicks2 extends SimplePicks {

    boolean isPicked(Var var);

    boolean isInvalidBuild();

    boolean isValidBuild();

    AssignmentException getException();

    String getErrorMessage();
}
