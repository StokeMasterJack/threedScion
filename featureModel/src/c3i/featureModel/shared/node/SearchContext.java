package c3i.featureModel.shared.node;

import c3i.featureModel.shared.VarComparator;
import c3i.featureModel.shared.boolExpr.Var;
import c3i.featureModel.shared.search.StopSearchException;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class SearchContext {

    public static final VarComparator VAR_COMPARATOR = new VarComparator();

    public VarComparator varComparator = new VarComparator();

    public ImmutableSet<Var> outVars;

    public Csp startNode;
    public int startLevel;

    public SearchContext(@Nonnull Csp startNode, @Nullable Integer startLevel, @Nullable Collection<Var> outVars, VarComparator varComparator) {
        checkNotNull(startNode);

        if (startLevel == null) {
            startLevel = 0;
        }

        if (varComparator == null) {
            varComparator = VAR_COMPARATOR;
        } else {
            this.varComparator = varComparator;
        }

        if (outVars == null) {
            this.outVars = ImmutableSet.copyOf(startNode.getContext().getVarList());
        } else {
            this.outVars = ImmutableSet.copyOf(outVars);
        }

        this.startNode = startNode;
    }

    public Csp getStartNode() {
        return startNode;
    }

    public int getStartLevel() {
        return startLevel;
    }

    public ImmutableSet<Var> getOutVars() {
        return outVars;
    }

    public void start() {
        this.onNode(startLevel, startNode);
    }

    public abstract void onNode(int level, Csp csp) throws StopSearchException;

}
