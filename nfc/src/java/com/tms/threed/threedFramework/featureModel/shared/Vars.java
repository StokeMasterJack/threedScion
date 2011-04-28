package com.tms.threed.threedFramework.featureModel.shared;

import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;

public interface Vars {

    int size();

    Var get(int varIndex) throws UnknownVarIndexException;

    Var get(String varCode) throws UnknownVarCodeException;

    boolean containsCode(String code);

    boolean containsIndex(int varIndex);




}
