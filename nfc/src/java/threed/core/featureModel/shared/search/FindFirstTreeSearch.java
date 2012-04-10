package threed.core.featureModel.shared.search;

import threed.core.featureModel.shared.Assignments;
import threed.core.featureModel.shared.CspForTreeSearch;
import threed.core.featureModel.shared.boolExpr.AssignmentException;
import threed.core.featureModel.shared.search.decision.Decision;
import threed.core.featureModel.shared.search.decision.Decisions;

/**
 * Copying-based branch-and-prune tree search
 */
public class FindFirstTreeSearch {

    private int decisionCount;

    public Assignments findFirst(CspForTreeSearch csp) {
        if(true) throw new UnsupportedOperationException();

        decisionCount = 0;

        try {
            nextDecisions(csp, 0);
        } catch (FoundFirstException e) {
            return e.getAssignments();
        } catch (AssignmentException e) {
            return null;
        }
        throw new IllegalStateException();
    }

    private void nextDecisions(CspForTreeSearch csp, int depth) {
        Decisions decisions = csp.decide2();
        if (decisions == null) {
            assert csp.isSolved1();
            assert csp.isTrue();

            throw new FoundFirstException(csp.getAssignments());
        } else {
            for (Decision decision : decisions) {
                CspForTreeSearch copy = csp.copy();
                processDecision(copy, decision, depth + 1);
            }
        }

    }

    private void processDecision(CspForTreeSearch csp, Decision decision, int depth) {
        decisionCount++;
        decision.makeAssignment(csp.getAssignments());
        csp.propagate();
        csp.simplify();
        assert !csp.isFalse();
        nextDecisions(csp, depth);
    }


    public static boolean hasAtLeastOneSolution(CspForTreeSearch csp) {
        FindFirstTreeSearch findFirstTreeSearch = new FindFirstTreeSearch();
        Assignments first = findFirstTreeSearch.findFirst(csp);
        return first != null;
    }

    public void printCounts() {
        System.out.println("decisionCount = " + decisionCount);
    }
}
