package c3i.core.featureModel.shared.search;

import c3i.core.common.shared.ProductHandler;
import c3i.core.featureModel.shared.CspForTreeSearch;
import c3i.core.featureModel.shared.search.decision.Decision;
import c3i.core.featureModel.shared.search.decision.Decisions;
import smartsoft.util.shared.Strings;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Copying-based branch-and-prune tree search
 * Uses out-vars
 * For AllSat and SatCount only
 * Use FindFirstTreeSearch for isSat
 */
public class TreeSearch {

    private int logLevel = 3;

    private ProductHandler<CspForTreeSearch> productHandler;
    private long solutionCount;
    private long visitCount;

    public TreeSearch(ProductHandler<CspForTreeSearch> productHandler) {
        this.productHandler = productHandler;
    }

    public TreeSearch() {
        this(null);
    }

    public void start(CspForTreeSearch csp) {
        search(csp, 0);
    }

    private void search(CspForTreeSearch csp, int depth) {
        visitCount++;
//        prindent1(depth, "search: " + csp);

        if (csp.isFalse()) {
            return;
        }

        if (csp.anyOpenOutputVars()) {
            if (csp.isTrue()) {
                onTrueNotOutComplete(csp, depth);
            } else if (csp.isOpen()) {
                onOpenNotOutComplete(csp, depth);
            } else {
                throw new IllegalStateException();
            }

        } else { //out complete
            if (csp.isTrue()) {
                onTrueOutComplete(csp, depth);
            } else if (csp.isOpen()) { //open
                onOpenOutComplete(csp, depth);
            } else {
                throw new IllegalStateException();
            }
        }


    }

    private void onTrueOutComplete(CspForTreeSearch csp, int depth) {
        solutionCount++;
        if (productHandler != null) {
            productHandler.onProduct(csp);
        }
    }

    private void onOpenOutComplete(CspForTreeSearch csp, int depth) {
        if (csp.isSat()) { //todo
            solutionCount++;
            if (productHandler != null) {
                productHandler.onProduct(csp);
            }
        }
    }

    //todo - this should uses Math.pow(2,dcCount);
    //todo - because all open vars, at this point, are dontCares
    private void onTrueNotOutComplete(CspForTreeSearch csp, int depth) {
        if (productHandler == null) {
            int dcCount = csp.getOpenOutputVarCount();
            solutionCount += twoToThePowerOf(dcCount);
        } else {
            distribute(csp, depth);
        }
    }

    private void onOpenNotOutComplete(CspForTreeSearch csp, int depth) {
        distribute(csp, depth);
    }

    private void distribute(CspForTreeSearch csp, int depth) {
        if (csp.isFalse()) {
            throw new IllegalArgumentException("distribute can only be passed a non-false csp. The following CSP is false: " + csp);
        }
        Decisions decisions = csp.decide();
//        prindent2(depth, "Decisions: " + decisions);
        checkNotNull(decisions);
        for (Decision decision : decisions) {
//            prindent2(depth, "Decision: " + decision);
//            prindent3(depth, "\t before assign \t[" + csp + "]");

            CspForTreeSearch copy = csp.refine(decision);
//            prindent3(depth, "\t after assign\t[" + copy + "]");
            search(copy, depth + 1);
        }
    }

    public long getSolutionCount() {
        return solutionCount;
    }

    public long getVisitCount() {
        return visitCount;
    }


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

    public static long twoToThePowerOf(int power) {
        return (long) Math.pow(2, power);
    }
}
