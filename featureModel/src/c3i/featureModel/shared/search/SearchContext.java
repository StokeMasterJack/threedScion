package c3i.featureModel.shared.search;

import c3i.featureModel.shared.VarComparator;
import c3i.featureModel.shared.boolExpr.Var;
import c3i.featureModel.shared.node.Csp;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;

public class SearchContext {

    private static final VarComparator VAR_COMPARATOR = new VarComparator();


    private final ImmutableSet<Var> outVars;

    private Csp startNode;

    public SearchContext(final Collection<Var> outVars) {
        this.outVars = ImmutableSet.copyOf(outVars);
    }

    public ImmutableSet<Var> getOutVars() {
        return outVars;
    }

    public void start() {

    }


}
