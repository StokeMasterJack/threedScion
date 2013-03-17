package c3i.featureModel.shared.common;

import c3i.featureModel.shared.boolExpr.Var;

/**
 * Should implement fast hash and equals
 */
public interface SimplePicks {

    boolean isPicked(Var var);

    boolean isValidBuild();
}
