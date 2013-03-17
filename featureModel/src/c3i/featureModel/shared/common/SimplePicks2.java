package c3i.featureModel.shared.common;

import c3i.featureModel.shared.boolExpr.AssignmentException;
import c3i.featureModel.shared.boolExpr.Var;
import c3i.featureModel.shared.common.SimplePicks;

public interface SimplePicks2 extends SimplePicks {

    boolean isPicked(Var var);

    boolean isInvalidBuild();

    boolean isValidBuild();

    AssignmentException getException();

    String getErrorMessage();
}
