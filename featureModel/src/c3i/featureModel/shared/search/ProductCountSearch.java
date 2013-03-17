package c3i.featureModel.shared.search;

import c3i.featureModel.shared.boolExpr.Var;
import c3i.featureModel.shared.node.Csp;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;

public class ProductCountSearch extends Search {

    private ImmutableSet<Var> outVars = ImmutableSet.of();

    private long productCount;

    public void setOutVars(Collection<Var> outVars) {
        this.outVars = ImmutableSet.copyOf(outVars);
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
            int dcCount = csp.getOpenOutVarCount();
            productCount += twoToThePowerOf(dcCount);
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

    public static long twoToThePowerOf(int power) {
        return (long) Math.pow(2, power);
    }


}
