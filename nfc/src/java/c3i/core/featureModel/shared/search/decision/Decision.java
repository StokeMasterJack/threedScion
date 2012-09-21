package c3i.core.featureModel.shared.search.decision;

import c3i.core.featureModel.shared.AssignContext;
import c3i.core.featureModel.shared.boolExpr.AssignmentException;

public interface Decision {

    void makeAssignment(AssignContext ctx) throws AssignmentException;


}
