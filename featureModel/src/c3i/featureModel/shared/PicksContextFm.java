package c3i.featureModel.shared;

import c3i.featureModel.shared.boolExpr.Var;
import c3i.featureModel.shared.node.Csp;
import c3i.featureModel.shared.picks.PicksContext;

public class PicksContextFm implements PicksContext {

    private Csp csp;
    private FeatureModel featureModel;

    public PicksContextFm(FeatureModel featureModel) {
        this.featureModel = featureModel;
        this.csp = featureModel.createCsp();
    }

    @Override
    public int getVarCount() {
        return featureModel.getVarCount();
    }

    @Override
    public Var getVarOrNull(String varCode) {
        return featureModel.getVarOrNull(varCode);
    }

    @Override
    public Csp getConstraint() {
        return csp;
    }

    @Override
    public Var getVar(int varIndex) {
        return featureModel.getVar(varIndex);
    }

    @Override
    public Var getVar(String varCode) {
        return featureModel.getVar(varCode);
    }
}
