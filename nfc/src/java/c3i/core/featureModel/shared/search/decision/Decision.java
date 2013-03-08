package c3i.core.featureModel.shared.search.decision;

import c3i.core.featureModel.shared.AssignContext;
import c3i.core.featureModel.shared.boolExpr.ReassignmentException;

public interface Decision {

    void makeAssignment(AssignContext ctx) throws ReassignmentException;


}
