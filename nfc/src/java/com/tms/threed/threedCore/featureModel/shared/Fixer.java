package com.tms.threed.threedCore.featureModel.shared;

import com.tms.threed.threedCore.featureModel.shared.boolExpr.AssignmentException;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;

import java.util.Collection;

public class Fixer {

    public static FixResult fix(FeatureModel fm, Collection<Var> trueVars) {
        Csp csp = null;

        try {

            csp = toCsp(fm, trueVars);
            csp.propagate();

            if (!csp.isSolved()) {
                csp.fillInDefaultValues();
            }

            AssignmentsSimple assignments = (AssignmentsSimple) csp.getAssignments();

            AssignmentsSimple copy = new AssignmentsSimple(assignments);

            return new FixResult(copy);

        } catch (AssignmentException e) {
            String errorMessage;
            if (csp == null) {
                errorMessage = e.getMessage();
            } else {
                errorMessage = e.getMessage(csp.getAssignments());
            }

            return new FixResult(errorMessage);
        }


    }

    private static CspSimple toCsp(FeatureModel featureModel, Collection<Var> trueVars) {
        assert trueVars != null;
        assert featureModel != null;

        CspSimple csp = featureModel.createCsp();

        for (Var trueVar : trueVars) {
            csp.assignTrue(trueVar);
        }

        return csp;

    }

}
