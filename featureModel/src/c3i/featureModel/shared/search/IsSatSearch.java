package c3i.featureModel.shared.search;

import c3i.featureModel.shared.boolExpr.Var;
import c3i.featureModel.shared.node.Csp;

public class IsSatSearch extends Search {

    private boolean sat = false;

    public void start(Csp node) {
        node.maybeSimplify();
        try {
            onNode(0, node);
        } catch (StopSearchException e) {
            sat = true;
        }
    }

    public boolean isSat() {
        return sat;
    }

    @Override
    public void onNode(int level, Csp csp) throws StopSearchException {
        if (csp.isFalse()) {
            //ignore
        } else if (csp.isTrue()) {
            throw new StopSearchException(csp);
        } else {
            Var var = csp.decide();
            onNode(level + 1, new Csp(csp, var, true));
            onNode(level + 1, new Csp(csp, var, false));
        }
    }


}
