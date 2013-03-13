package c3i.featureModel.shared;

import c3i.featureModel.shared.boolExpr.AssignmentException;
import c3i.featureModel.shared.boolExpr.Var;

public interface AutoAssignContext extends EvalContext, AssignContext {

    @Override
    void assignTrue(Var var) throws AssignmentException;

    @Override
    void assignTrue(Var var, int depth) throws AssignmentException;

    @Override
    void assignFalse(Var var) throws AssignmentException;

    @Override
    void assignFalse(Var var, int depth) throws AssignmentException;

    AutoAssignContext copy();

}
