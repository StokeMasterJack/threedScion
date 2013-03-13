package c3i.featureModel.shared.picks;

import c3i.featureModel.shared.FeatureModel;
import c3i.featureModel.shared.boolExpr.MasterConstraint;
import c3i.featureModel.shared.boolExpr.Var;

public class PicksContextFm implements PicksContext {

    private final FeatureModel fm;

    public PicksContextFm(FeatureModel fm) {
        this.fm = fm;
    }

    @Override
    public int getVarCount() {
        return fm.size();
    }

    @Override
    public Var getVarOrNull(String varCode) {
        return fm.resolveVar(varCode);
    }

    @Override
    public MasterConstraint getConstraint() {
        return fm.getConstraint();
    }

    @Override
    public Var getVar(int varIndex) {
        return fm.get(varIndex);
    }

    @Override
    public Var getVar(String varCode) {
        return fm.get(varCode);
    }
}
