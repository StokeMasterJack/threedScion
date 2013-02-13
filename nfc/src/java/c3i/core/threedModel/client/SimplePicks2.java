package c3i.core.threedModel.client;

import c3i.core.featureModel.shared.boolExpr.AssignmentException;
import c3i.core.featureModel.shared.boolExpr.Var;

public interface SimplePicks2 {

    boolean isPicked(Var var);

    boolean isInvalidBuild();

    boolean isValidBuild();

    AssignmentException getException();

    String getErrorMessage();
}
