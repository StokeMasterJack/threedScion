package c3i.featureModel.shared.search;

import c3i.featureModel.shared.CspForTreeSearch;
import c3i.featureModel.shared.boolExpr.AssignmentException;
import c3i.featureModel.shared.search.decision.Decision;
import c3i.featureModel.shared.search.decision.Decisions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Copying-based branch-and-prune tree search
 */
public class FindFirstTreeSearch {

    private CspForTreeSearch firstSolution;

    public FindFirstTreeSearch(CspForTreeSearch csp) {

        try {
            search(csp, 0);

            //visited every possible node and found no solution
            checkState(firstSolution == null);

            //i think this should never occur
            throw new IllegalStateException();

        } catch (FoundFirstException e) {
            //must have found a solution - stop searching
            checkState(firstSolution != null);

        } catch (AssignmentException e) {
            //stop searching
            checkState(firstSolution == null);
        }

    }

    public CspForTreeSearch getFirstSolution() {
        return firstSolution;
    }

    private void search(CspForTreeSearch csp, int depth) {
        if (csp.isTrue()) {
            firstSolution = csp;
            throw new FoundFirstException();
        } else if (csp.isFalse()) {
            //do not descend
        } else if (csp.isOpen()) {
            distribute(csp, depth + 1);
        } else { //out complete
            throw new IllegalStateException();
        }
    }

    private void distribute(CspForTreeSearch csp, int depth) {
        if (csp.isFalse()) {
            throw new IllegalArgumentException("distribute can only be passed a non-false csp. The following CSP is false: " + csp);
        }
        Decisions decisions = csp.decide();
        checkNotNull(decisions);
        for (Decision decision : decisions) {
            CspForTreeSearch copy = csp.refine(decision);
            search(copy, depth + 1);
        }
    }


    private static class FoundFirstException extends RuntimeException {

    }
}
