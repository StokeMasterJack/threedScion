package c3i.featureModel.shared;

import c3i.featureModel.shared.boolExpr.ConflictingAssignmentException;
import c3i.featureModel.shared.boolExpr.Var;
import c3i.featureModel.shared.explanations.Cause;

public interface AssignContext {

    void assign(Var var, boolean value, Cause cause) throws ConflictingAssignmentException;


}
