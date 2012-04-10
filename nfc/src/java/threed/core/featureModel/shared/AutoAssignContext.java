package threed.core.featureModel.shared;

import threed.core.featureModel.shared.boolExpr.AssignmentException;
import threed.core.featureModel.shared.boolExpr.Var;

public interface AutoAssignContext extends EvalContext, AssignContext {

    @Override
    void assignTrue(Var var) throws AssignmentException;

    @Override
    void assignTrue(Var var,int depth) throws AssignmentException;

    @Override
    void assignFalse(Var var) throws AssignmentException;

    @Override
    void assignFalse(Var var,int depth) throws AssignmentException;

    AutoAssignContext copy();

}
