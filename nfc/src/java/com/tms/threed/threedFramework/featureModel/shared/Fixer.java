package com.tms.threed.threedFramework.featureModel.shared;

import com.tms.threed.threedFramework.featureModel.shared.boolExpr.AssignmentException;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;

import java.util.Collection;
import java.util.Set;

public class Fixer {

    public static FixResult fix(FeatureModel fm, Collection<Var> trueVars) {
        AbstractCsp csp = null;

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

    private static Csp toCsp(FeatureModel featureModel, Collection<Var> trueVars) {
        assert trueVars != null;
        assert featureModel != null;

        Csp csp = featureModel.createCsp();

        for (Var trueVar : trueVars) {
            csp.assignTrue(trueVar);
        }

        return csp;

    }

}
