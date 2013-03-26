package c3i.featureModel.shared.search;

import c3i.featureModel.shared.boolExpr.Var;
import c3i.featureModel.shared.common.SimplePicks;
import c3i.featureModel.shared.node.Csp;

import java.util.LinkedList;

public class ForEachDontCareSearch implements SimplePicks {

    private final Csp solutionNode;
    private final ProductHandler productHandler;

    private final LinkedList<Var> dcAssignments = new LinkedList<Var>();
    private long productCount;

    public ForEachDontCareSearch(Csp solutionNode, ProductHandler productHandler) {
        this.solutionNode = solutionNode;
        this.productHandler = productHandler;
    }

    public void start(int depth) {
        onDontCareNode(depth);
    }

    public void onDontCareNode(int level) {
        Var var = solutionNode.decide();
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
        return solutionNode.isPicked(var) || dcAssignments.contains(var);
    }

}
