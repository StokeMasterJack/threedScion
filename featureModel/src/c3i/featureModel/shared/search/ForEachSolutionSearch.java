package c3i.featureModel.shared.search;

import c3i.featureModel.shared.boolExpr.Var;
import c3i.featureModel.shared.explanations.Cause;
import c3i.featureModel.shared.node.Csp;
import c3i.featureModel.shared.node.SearchContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
public abstract class ForEachSolutionSearch extends SearchContext {

    public ForEachSolutionSearch(@Nonnull Csp startNode, @Nullable Collection<Var> outVars) {
        super(startNode, null, outVars, null);
    }

    @Override
    public void start() {
        startNode.processDirtyQueue();
        super.start();
    }

    @Override
    public void onNode(int level, final Csp csp) {

        if (csp.isFalse()) {
            return;
        }

        if (csp.isSolution()) {
            onSolution(level, csp);
        } else {
            Var var = csp.decide();

            onNode(level + 1, new Csp(csp, var, true, Cause.DECISION));
            onNode(level + 1, new Csp(csp, var, false, Cause.DECISION));
        }


    }

    public abstract void onSolution(int level, Csp solutionCsp);

}
