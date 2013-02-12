package c3i.core.imageModel.shared;

import c3i.core.featureModel.shared.boolExpr.Var;

/**
 * Should implement fast hash and equals
 */
public interface SimplePicks {

    boolean isPicked(Var var);

    boolean isValidBuild();
}
