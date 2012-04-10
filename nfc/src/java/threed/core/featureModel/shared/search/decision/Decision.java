package threed.core.featureModel.shared.search.decision;

import threed.core.featureModel.shared.AssignContext;
import threed.core.featureModel.shared.boolExpr.AssignmentException;

public interface Decision {

    void makeAssignment(AssignContext ctx) throws AssignmentException;


}
