package c3i.featureModel.shared;

import c3i.featureModel.shared.boolExpr.ReassignmentException;
import c3i.featureModel.shared.boolExpr.Var;

public interface AssignContext {

    void assignTrue(Var var) throws ReassignmentException;

    void assignTrue(Var var, int depth) throws ReassignmentException;

    void assignFalse(Var var) throws ReassignmentException;

    void assignFalse(Var var, int depth) throws ReassignmentException;

    AssignContext copy();


}
