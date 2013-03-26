package c3i.featureModel.shared.node;

import c3i.featureModel.shared.FeatureModel;
import c3i.featureModel.shared.boolExpr.BoolExpr;
import c3i.featureModel.shared.boolExpr.Var;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.HashSet;
import java.util.LinkedHashSet;

public class FmCspContext implements GlobalContext {

    private final ImmutableList<Var> varList;
    private final ImmutableMap<String, Var> varMap;

    private final ImmutableList<BoolExpr> constraints;
    private final ImmutableList<BoolExpr> assignments;

    public FmCspContext(FeatureModel featureModel) {

        varList = featureModel.getVarList();
        varMap = featureModel.getVarMap();

        LinkedHashSet<BoolExpr> allConstraints = featureModel.getAllConstraints();

        HashSet<BoolExpr> complex = new HashSet<BoolExpr>();
        HashSet<BoolExpr> simple = new HashSet<BoolExpr>();

        for (BoolExpr expr : allConstraints) {
            if (expr.isComplex()) {
                complex.add(expr);
            } else if (expr.isSimple()) {
                simple.add(expr);
            } else {
                //ignore constant
            }
        }

        this.constraints = ImmutableList.copyOf(complex);
        this.assignments = ImmutableList.copyOf(simple);
    }

    @Override
    public int getVarCount() {
        return varList.size();
    }

    @Override
    public int getConstraintCount() {
        return constraints.size();
    }

    @Override
    public Var getVar(int varIndex) {
        return varList.get(varIndex);
    }

    @Override
    public BoolExpr getConstraint(int i) {
        return constraints.get(i);
    }

    @Override
    public ImmutableList<BoolExpr> getComplexConstraints() {
        return constraints;
    }

    @Override
    public ImmutableList<BoolExpr> getSimpleConstraints() {
        return assignments;
    }

    @Override
    public Var getVar(String varCode) {
        return varMap.get(varCode);
    }

    public ImmutableList<Var> getVarList() {
        return varList;
    }

    public ImmutableMap<String, Var> getVarMap() {
        return varMap;
    }
}
