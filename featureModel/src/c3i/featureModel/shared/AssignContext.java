package c3i.featureModel.shared;

import c3i.featureModel.shared.boolExpr.ConflictingAssignmentException;
import c3i.featureModel.shared.boolExpr.Var;

public interface AssignContext {

    boolean assignTrue(Var var) throws ConflictingAssignmentException;

    boolean assignFalse(Var var) throws ConflictingAssignmentException;

    boolean assign(Var var, boolean value) throws ConflictingAssignmentException;


}
