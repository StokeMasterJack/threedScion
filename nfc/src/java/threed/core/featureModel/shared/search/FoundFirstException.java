package threed.core.featureModel.shared.search;

import threed.core.featureModel.shared.Assignments;

public class FoundFirstException extends RuntimeException {

    private final Assignments assignments;

    public FoundFirstException(Assignments assignments) {
        this.assignments = assignments;
    }

    public Assignments getAssignments() {
        return assignments;
    }

}
