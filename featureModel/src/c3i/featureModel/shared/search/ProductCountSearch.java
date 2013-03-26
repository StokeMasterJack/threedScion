package c3i.featureModel.shared.search;

import c3i.featureModel.shared.boolExpr.Var;
import c3i.featureModel.shared.node.Csp;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public class ProductCountSearch extends ForEachSolutionSearch {

    private long productCount;

    public ProductCountSearch(@Nonnull Csp startNode, @Nullable Collection<Var> outVars) {
        super(startNode, outVars);
    }

    @Override
    public void onSolution(int level, Csp solutionCsp) {
        int dcCount = solutionCsp.getOpenOutVarCount();
        productCount += twoToThePowerOf(dcCount);
    }

    public long getProductCount() {
        return productCount;
    }

    public static long twoToThePowerOf(int power) {
        return (long) Math.pow(2, power);
    }

}
