package c3i.featureModel.shared.search;

import c3i.featureModel.shared.boolExpr.Var;
import c3i.featureModel.shared.node.Csp;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public class ForEachProductSolutionSearch extends ForEachSolutionSearch {

    private ProductHandler productHandler;

    public ForEachProductSolutionSearch(@Nonnull Csp startNode, @Nullable Collection<Var> outVars, ProductHandler productHandler) {
        super(startNode, outVars);
        this.productHandler = productHandler;
    }

    @Override
    public void onSolution(int level, Csp solutionCsp) {
        ForEachDontCareSearch dcSearch = new ForEachDontCareSearch(solutionCsp, productHandler);
        dcSearch.onDontCareNode(level);
    }

}
