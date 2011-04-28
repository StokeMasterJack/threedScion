package com.tms.threed.threedFramework.featureModel.shared;

import com.tms.threed.threedFramework.featureModel.shared.boolExpr.AssignmentException;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;

public interface AssignContext {

    void assignTrue(Var var) throws AssignmentException;
    void assignTrue(Var var,int depth) throws AssignmentException;

    void assignFalse(Var var) throws AssignmentException;
    void assignFalse(Var var,int depth) throws AssignmentException;


}
