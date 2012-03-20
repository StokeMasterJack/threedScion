package com.tms.threed.threedCore.featureModel.shared;

import com.tms.threed.threedCore.featureModel.shared.boolExpr.AssignmentException;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;

public interface AutoAssignContext extends EvalContext, AssignContext {

    @Override
    void assignTrue(Var var) throws AssignmentException;

    @Override
    void assignTrue(Var var,int depth) throws AssignmentException;

    @Override
    void assignFalse(Var var) throws AssignmentException;

    @Override
    void assignFalse(Var var,int depth) throws AssignmentException;


}
