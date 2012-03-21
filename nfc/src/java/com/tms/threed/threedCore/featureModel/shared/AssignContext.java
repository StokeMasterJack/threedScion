package com.tms.threed.threedCore.featureModel.shared;

import com.tms.threed.threedCore.featureModel.shared.boolExpr.AssignmentException;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;

public interface AssignContext {

    void assignTrue(Var var) throws AssignmentException;
    void assignTrue(Var var,int depth) throws AssignmentException;

    void assignFalse(Var var) throws AssignmentException;
    void assignFalse(Var var,int depth) throws AssignmentException;

    AssignContext copy();


}
