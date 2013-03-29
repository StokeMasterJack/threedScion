package c3i.featureModel.shared.search;

import c3i.featureModel.shared.boolExpr.Var;
import c3i.featureModel.shared.explanations.Cause;
import c3i.featureModel.shared.node.Csp;
import c3i.featureModel.shared.node.SearchContext;

import static com.google.common.base.Preconditions.checkState;

public class IsSatSearch extends SearchContext {

    private boolean sat = false;

    public IsSatSearch(Csp startNode) {
        super(startNode, 0, null, null);
    }

    public void execute() {

        try {
            super.execute();
            sat = false;
        } catch (StopSearchException e) {
            sat = true;
        }
    }

    public boolean isSat() {
        return sat;
    }

    public void onNode(int level, Csp csp) throws StopSearchException {
        assert csp.searchContext == this;
        checkState(csp.isStable());
        csp.checkOpenClauseCount();

        if (csp.isTrue()) {
            throw new StopSearchException(csp);
        } else if (csp.isFalse()) {
            //skip
        } else { //open
            Var var = null;
            try {
                var = csp.decide();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            checkState(var != null); //should never occur
            onNode(level + 1, new Csp(csp, var, true, Cause.DECISION));
            onNode(level + 1, new Csp(csp, var, false, Cause.DECISION));
        }

    }


}

