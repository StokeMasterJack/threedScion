package com.tms.threed.threedCore.featureModel.shared;

import com.tms.threed.threedCore.threedModel.client.SimplePicks2;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;

public class FixResult implements SimplePicks2 {

    private final Assignments assignments;
    private final String errorMessage;

    public FixResult(Assignments assignments) {
        this.assignments = assignments;
        errorMessage = null;
    }

    public FixResult(String errorMessage) {
        this.assignments = null;
        this.errorMessage = errorMessage;
    }

    public FixResult(FixResult that) {
        if (that.isInvalidBuild()) {
            this.assignments = null;
            this.errorMessage = that.errorMessage;
        }else{
            this.errorMessage = null;
            this.assignments = new AssignmentsSimple((AssignmentsSimple) that.assignments);
        }

    }

    public Assignments getAssignments() {
        return assignments;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isPicked(Var var) {
        assert assignments != null;
        return assignments.isPicked(var);
    }

    public boolean isValidBuild() {
        if (errorMessage == null && assignments != null) {
            return true;
        } else if (errorMessage != null && assignments == null) {
            return false;
        } else {
            throw new IllegalStateException();
        }
    }

    public boolean isInvalidBuild() {
        return !isValidBuild();
    }

    @Override public String toString() {
        if (isValidBuild()) return "Valid build";
        else return "Invalid build: " + errorMessage;
    }

    public String toStringLong() {
        if (isValidBuild()) return "Valid build: " + assignments.getTrueVars();
        else return "Invalid build: " + errorMessage;
    }
}
