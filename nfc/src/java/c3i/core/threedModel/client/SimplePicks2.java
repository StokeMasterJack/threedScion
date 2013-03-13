package c3i.core.threedModel.client;

import c3i.featureModel.shared.boolExpr.AssignmentException;
import c3i.featureModel.shared.boolExpr.Var;
import c3i.imageModel.shared.SimplePicks;

public interface SimplePicks2 extends SimplePicks<Var> {

    boolean isPicked(Var var);

    boolean isInvalidBuild();

    boolean isValidBuild();

    AssignmentException getException();

    String getErrorMessage();
}
