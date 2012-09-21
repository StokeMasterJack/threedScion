package c3i.core.featureModel.shared.search;

import c3i.core.featureModel.shared.Assignments;

public class FoundFirstException extends RuntimeException {

    private final Assignments assignments;

    public FoundFirstException(Assignments assignments) {
        this.assignments = assignments;
    }

    public Assignments getAssignments() {
        return assignments;
    }

}
