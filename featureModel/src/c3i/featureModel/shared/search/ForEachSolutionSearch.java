package c3i.featureModel.shared.search;

import c3i.featureModel.shared.boolExpr.Var;
import c3i.featureModel.shared.node.Csp;
import c3i.featureModel.shared.node.ForEachProductSearch;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;

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
public class ForEachSolutionSearch extends Search {

    private ImmutableSet<Var> outVars = ImmutableSet.of();

    private ProductHandler productHandler;

    private ForEachProductSearch forEachProductSearch;

    private long productCount;

    public void setOutVars(Collection<Var> outVars) {
        this.outVars = ImmutableSet.copyOf(outVars);
    }

    public void setProductHandler(ProductHandler productHandler) {
        this.productHandler = productHandler;
    }

    public void start(Csp node) {
        node.maybeSimplify();
        onNode(0, node);
    }

    @Override
    public void onNode(int level, Csp csp) {

        if (csp.isFalse()) {
            return;
        }

        if (csp.isSolution()) {
            ForEachProductSearch forEachProductSearch = new ForEachProductSearch();
            forEachProductSearch.onNode(level, csp);
        } else {
            Var var = csp.decide();

            onNode(level + 1, new Csp(csp, var, true));
            onNode(level + 1, new Csp(csp, var, false));
        }


    }


    public long getProductCount() {
        return productCount;
    }


    public ImmutableSet<Var> getOutVars() {
        return outVars;
    }
}
