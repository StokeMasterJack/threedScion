package c3i.featureModel.shared;

import c3i.featureModel.shared.boolExpr.AssignmentException;
import c3i.featureModel.shared.boolExpr.Var;

public interface AutoAssignContext extends EvalContext, AssignContext {

    boolean assign(Var var, boolean value) throws AssignmentException;


    AutoAssignContext copy();
}
