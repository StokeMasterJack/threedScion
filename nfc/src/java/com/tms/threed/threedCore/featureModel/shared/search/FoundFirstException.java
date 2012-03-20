package com.tms.threed.threedCore.featureModel.shared.search;

import com.tms.threed.threedCore.featureModel.shared.Assignments;

public class FoundFirstException extends RuntimeException {

    private final Assignments assignments;

    public FoundFirstException(Assignments assignments) {
        this.assignments = assignments;
    }

    public Assignments getAssignments() {
        return assignments;
    }

}
