package com.tms.threed.threedCore.featureModel.shared;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.AssignmentException;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedCore.threedModel.client.SimplePicks2;

public class FixResult implements SimplePicks2{

    private final ImmutableSet<String> pickRaw;
    private final ImmutableSet<Var> picks;

    private final Assignments assignments;
    private final AssignmentException exception;

    public FixResult(ImmutableSet<String> pickRaw, ImmutableSet<Var> picks, Assignments assignments, AssignmentException exception) {
        Preconditions.checkArgument((assignments == null && exception != null) ||
                (assignments != null && exception == null));

        this.pickRaw = pickRaw;
        this.picks = picks;
        this.assignments = assignments;
        this.exception = exception;
    }


    public FixResult(Assignments assignments) {
        this(null, null, assignments, null);
    }

    public FixResult(AssignmentException exception) {
        this(null, null, null, exception);
    }

    public FixResult(FixResult that) {
        this.pickRaw = that.pickRaw;
        this.picks = that.picks;
        this.assignments = that.assignments;
        this.exception = that.exception;
    }

    public Assignments getAssignments() {
        return assignments;
    }

    public AssignmentException getException() {
        return exception;
    }

    public String getErrorMessage() {
        if (exception == null) {
            return null;
        } else {
            return exception.getMessage();
        }
    }

    public boolean isPicked(Var var) {
        assert assignments != null;
        return assignments.isPicked(var);
    }

    public boolean isValidBuild() {
        if (exception == null && assignments != null) {
            return true;
        } else if (exception != null && assignments == null) {
            return false;
        } else {
            throw new IllegalStateException();
        }
    }

    public boolean isInvalidBuild() {
        return !isValidBuild();
    }

    @Override
    public String toString() {
        if (isValidBuild()) return "Valid build";
        else return "Invalid build: " + exception;
    }

    public String toStringLong() {
        if (isValidBuild()) return "Valid build: " + assignments.getTrueVars();
        else return "Invalid build: " + exception;
    }

    public ImmutableSet<String> getPickRaw() {
        return pickRaw;
    }

    public ImmutableSet<Var> getPicks() {
        return picks;
    }




}
