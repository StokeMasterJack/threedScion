package c3i.featureModel.shared.search;

import c3i.featureModel.shared.boolExpr.Var;
import c3i.featureModel.shared.explanations.Cause;
import c3i.featureModel.shared.node.Csp;

import static com.google.common.base.Preconditions.checkState;

public class IsSatSearch extends Search {

    private boolean sat = false;

    private Csp contextCsp;

    public IsSatSearch(Csp contextCsp) {
        this.contextCsp = contextCsp;
        startSearch();
    }

    private void startSearch() {
        try {
            onNode(0, contextCsp);
            sat = false;
        } catch (StopSearchException e) {
            sat = true;
        }
    }

    public boolean isSat() {
        return sat;
    }

    int nodeCount = 0;

    public void onNode(int level, Csp csp) throws StopSearchException {

        checkState(csp.isStable());
        csp.checkOpenClauseCount();

//        System.out.println(csp.toString());
        nodeCount++;
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

