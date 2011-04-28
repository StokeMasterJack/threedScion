package com.tms.threed.threedFramework.featureModel.shared.search;

import com.tms.threed.threedFramework.featureModel.shared.Assignments;

public class FoundFirstException extends RuntimeException {

    private final Assignments assignments;

    public FoundFirstException(Assignments assignments) {
        this.assignments = assignments;
    }

    public Assignments getAssignments() {
        return assignments;
    }

}
