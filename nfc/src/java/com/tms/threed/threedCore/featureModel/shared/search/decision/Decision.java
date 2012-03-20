package com.tms.threed.threedCore.featureModel.shared.search.decision;

import com.tms.threed.threedCore.featureModel.shared.AssignContext;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.AssignmentException;

public interface Decision {

    void makeAssignment(AssignContext ctx) throws AssignmentException;


}
