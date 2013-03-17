package c3i.featureModel.shared.node;

import c3i.featureModel.shared.FeatureModel;
import c3i.featureModel.shared.VarSpace;
import c3i.featureModel.shared.boolExpr.BoolExpr;
import c3i.featureModel.shared.boolExpr.Var;

import java.util.LinkedHashSet;

public abstract class FmCspContext implements CspContext {

    private final FeatureModel featureModel;

    protected FmCspContext(FeatureModel featureModel) {
        this.featureModel = featureModel;
    }

    public abstract VarSpace getVarSpace();

    public abstract int getVarCount();

    public abstract Var getVar(int varIndex);

    public LinkedHashSet<BoolExpr> getConstraints() {
        return featureModel.getConstraints();
    }

}
