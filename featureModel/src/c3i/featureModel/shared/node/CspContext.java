package c3i.featureModel.shared.node;

import c3i.featureModel.shared.boolExpr.BoolExpr;
import c3i.featureModel.shared.boolExpr.Var;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Instances of CspContext should be immutable because it is shallow copied during search
 */
public interface CspContext {

    int getVarCount();

    int getConstraintCount();

    Var getVar(int varIndex);

    Var getVar(String varCode);

    /**
     * Complex constraint
     */
    BoolExpr getConstraint(int i);

    /**
     * Complex constraints
     */
    ImmutableList<BoolExpr> getComplexConstraints();

    ImmutableList<BoolExpr> getSimpleConstraints();

    ImmutableList<Var> getVarList();

    ImmutableMap<String, Var> getVarMap();
}
