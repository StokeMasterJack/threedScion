package c3i.featureModel.shared.search.decision;

import c3i.featureModel.shared.AssignContext;
import c3i.featureModel.shared.boolExpr.ReassignmentException;

public interface Decision {

    void makeAssignment(AssignContext ctx) throws ReassignmentException;


}
