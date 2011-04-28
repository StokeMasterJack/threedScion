package com.tms.threed.threedFramework.featureModel.shared.search.decision;

import com.tms.threed.threedFramework.featureModel.shared.AssignContext;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.AssignmentException;

public interface Decision {

    void makeAssignment(AssignContext ctx) throws AssignmentException;


}
