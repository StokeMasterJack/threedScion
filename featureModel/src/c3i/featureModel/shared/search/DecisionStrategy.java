package c3i.featureModel.shared.search;

import c3i.featureModel.shared.boolExpr.Var;
import c3i.featureModel.shared.node.Csp;

public interface DecisionStrategy {

    /**
     * @param csp
     * @return to indicate that search is out complete
     */
    Var decide(Csp csp);

}
