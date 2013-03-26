package c3i.featureModel.shared.search;

import c3i.featureModel.shared.boolExpr.Var;
import c3i.featureModel.shared.common.SimplePicks;
import c3i.featureModel.shared.explanations.Cause;
import c3i.featureModel.shared.node.Csp;
import com.google.common.collect.ImmutableSet;

import java.util.LinkedList;

/**
 *  Called for any node where csp.isSolution() where:
 *      Csp.isSolution() =>  isTrue() || (isOutComplete() && isSat());
 *
 *      if a node becomes True before it becomes outComplete
 *          then all of its openOutVars will are dontCares: we need to iterate the dontCares
 *
 *      if a node becomes OutComplete before (at the same time) it becomes True
 *          then there will be no openOutVars
 *          we are on a Product node - we just need to stamp a Product
 *
 */

/**
 *  This NodeHandler is meant to be called "onSolution" where
 *      Csp.isSolution() =>  isTrue() || (isOutComplete() && isSat());
 *      some outVars are still in the dontCare state.
 *  Rather than instantiating a whole csp for each pureDontCare node, we can use a lighter data structure
 */
public class ForEachSolutionSearch extends OutSearch {

    private ProductHandler productHandler;

    private ForEachProductSearch forEachProductSearch;

    private long productCount;

    public void setProductHandler(ProductHandler productHandler) {
        this.productHandler = productHandler;
    }

    public void start(Csp node) {
        node.processDirtyQueue();
        onNode(0, node);
    }

    @Override
    public void onNode(int level, final Csp csp) {

        if (csp.isFalse()) {
            return;
        }

        if (csp.isSolution()) {
            ForEachProductSearch search = new ForEachProductSearch(csp, productHandler);
            search.onDontCareNode(level);

        } else {
            Var var = csp.decide();

            onNode(level + 1, new Csp(csp, var, true, Cause.DECISION));
            onNode(level + 1, new Csp(csp, var, false, Cause.DECISION));
        }


    }

    public long getProductCount() {
        return productCount;
    }



    public static class ForEachProductSearch implements SimplePicks {

        private final Csp csp;
        private final ProductHandler productHandler;

        private final LinkedList<Var> dcAssignments = new LinkedList<Var>();
        private long productCount;

        public ForEachProductSearch(Csp csp, ProductHandler productHandler) {
            this.csp = csp;
            this.productHandler = productHandler;
        }

        public void start(int depth) {
            onDontCareNode(depth);
        }

        public void onDontCareNode(int level) {
            Var var = csp.decide();
            if (var == null) {
                productCount++;
                productHandler.onProduct(this);
            } else {
                dcAssignments.addLast(var);
                onDontCareNode(level + 1);
                dcAssignments.removeLast();
                onDontCareNode(level + 1);
            }

        }

        @Override
        public boolean isPicked(Var var) {
            return csp.isPicked(var) || dcAssignments.contains(var);
        }

    }

}
