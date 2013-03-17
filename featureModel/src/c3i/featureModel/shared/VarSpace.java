package c3i.featureModel.shared;

import c3i.featureModel.shared.boolExpr.Var;

public interface VarSpace extends Iterable<Var> {

    int size();

    Var getVar(int varIndex) throws UnknownVarIndexException;

    Var getVar(String varCode) throws UnknownVarCodeException;

    boolean containsCode(String code);

    boolean containsIndex(int varIndex);


}
