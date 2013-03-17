package c3i.featureModel.shared.node;

import c3i.featureModel.shared.VarSpace;
import c3i.featureModel.shared.boolExpr.BoolExpr;
import c3i.featureModel.shared.boolExpr.Var;

import java.util.LinkedHashSet;

public interface CspContext {

    VarSpace getVarSpace();

    int getVarCount();

    Var getVar(int varIndex);

    Var getVar(String varCode);

    public LinkedHashSet<BoolExpr> getConstraints();


    int size();
}
