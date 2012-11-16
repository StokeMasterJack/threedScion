package c3i.core.featureModel.shared.search;

import c3i.core.featureModel.shared.Assignments;
import c3i.core.featureModel.shared.AssignmentsForTreeSearch;
import c3i.core.featureModel.shared.CspForTreeSearch;
import c3i.core.featureModel.shared.boolExpr.AssignmentException;
import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.core.featureModel.shared.search.decision.Decision;
import c3i.core.featureModel.shared.search.decision.Decisions;
import smartsoft.util.shared.Strings;

import java.util.Set;

/**
 * Copying-based branch-and-prune tree search
 */
public class TreeSearch {

    private ProductHandler productHandler;

    public Set<Var> jpgVars;

    private long solutionCount;
    private int rejectCount;
    private int decisionsCount;
    private int decisionCount;
    private int propagateCount;
    private int skipSimplifyCount;
    private int decisionsNullConstraintTrue;
    private int decisionsNullConstraintFalse;
    private int decisionsNullConstraintOpen;

    public void setProductHandler(ProductHandler productHandler) {
        this.productHandler = productHandler;
    }

    public void start(CspForTreeSearch csp) {
        nextDecisions(csp, 0);
    }

    int aaTrue;
    int bbTrue;
    int ccFalse;
    int ddOpen1;
    int ddOpen2;
    int eeException;

    private boolean canBeExtendedToTrue(CspForTreeSearch csp) {

        //this seem to always return true for some reason, thus:

        if (true) return true;

        boolean canBeExtended;

        FindFirstTreeSearch findFirstSearch = new FindFirstTreeSearch();

        Assignments before = csp.getAssignments().copy(csp.getOpenVars().copy());
        Assignments after = findFirstSearch.findFirst(csp.copy());

        if (after == null) {
            System.out.println("Cannot be extended: " + before.getTrueVars());
            canBeExtended = false;
        } else {
            canBeExtended = true;
        }


        return canBeExtended;


    }

    public void nextDecisions(CspForTreeSearch csp, int depth) {

        if (csp.anyOpenOutputVars()) {

            Decisions decisions = csp.decide();
            decisionsCount++;
            //prindent(depth, System.identityHashCode(this) + ".Decisions: " + decisions);

            for (Decision decision : decisions) {
                CspForTreeSearch copy = csp.copy();
                processDecision(copy, decision, depth + 1);
            }

        } else {

            assert csp.getOpenOutputVarCount() == 0;

            if (csp.isTrue()) {
                aaTrue++;
                onCompleteSolution(csp.getAssignments());
            } else if (csp.isFalse()) {
                //skip
            } else { //open
                if (canBeExtendedToTrue(csp)) { //this is stubbed out to always return true
                    onCompleteSolution(csp.getAssignments());
                }
            }
        }

    }


    private void processDecision(CspForTreeSearch csp, Decision decision, int depth) {
        this.decisionCount++;

//        prindent2(depth, "Process decision: " + decision);
//        prindent3(depth, "\t before assign \t[" + csp + "]");


        try {
            decision.makeAssignment(csp.getAssignments());
            this.propagateCount++;
            csp.propagateSimplify();

//            prindent3(depth, "\t after assign\t[" + csp + "]");
            nextDecisions(csp, depth);
        } catch (AssignmentException e) {
            rejectCount++;
        }

    }


    protected void onCompleteSolution(AssignmentsForTreeSearch completeSolution) {
        solutionCount++;
        if (productHandler != null) {
            productHandler.onProduct(completeSolution);
        }

//        if (solutionCount % 1000000L == 0) {
//            System.out.println("solutionCount = " + solutionCount);
//        }
//        Set<Var> product = completeSolution.getTrueOutputVars(varsToSuppressInSolution);
//        boolean added = uniqueSolutions.add(product.toString());


    }

    public long getSolutionCount() {
        return solutionCount;
    }

    public int getRejectCount() {
        return rejectCount;
    }

    private int logLevel = 3;

    public void prindent(int depth, String msg) {
        System.err.println(Strings.indent(depth) + msg);
    }

    public void prindent1(int depth, String msg) {
        if (logLevel >= 1) prindent(depth, msg);
    }

    public void prindent2(int depth, String msg) {
        if (logLevel >= 2) prindent(depth, msg);
    }

    public void prindent3(int depth, String msg) {
        if (logLevel >= 3) prindent(depth, msg);
    }

    public int getDecisionsCount() {
        return decisionsCount;
    }

    public int getDecisionCount() {
        return decisionCount;
    }

    public int getPropagateCount() {
        return propagateCount;
    }

    public void printCounts() {
        System.err.println("solutionCount = " + solutionCount);
        System.err.println("rejectCount = " + rejectCount);
        System.err.println("decisionsCount = " + decisionsCount);
        System.err.println("decisionCount = " + decisionCount);
        System.err.println("propagateCount = " + propagateCount);
        System.err.println("skipSimplifyCount = " + skipSimplifyCount);

        System.err.println("aaTrue = " + aaTrue);
        System.err.println("bbTrue = " + bbTrue);
        System.err.println("ccFalse = " + ccFalse);
        System.err.println("ddOpen1 = " + ddOpen1);
        System.err.println("ddOpen2 = " + ddOpen2);
        System.err.println("eeException = " + eeException);


        System.err.println();

    }
}
