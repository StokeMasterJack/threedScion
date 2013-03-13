package c3i.featureModel.shared;

import c3i.featureModel.shared.boolExpr.AssignmentException;
import c3i.featureModel.shared.boolExpr.Var;
import c3i.core.threedModel.client.SimplePicks2;
import c3i.util.shared.futures.HasKey;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Set;

@Immutable
public class FixedPicks implements SimplePicks2, HasKey {

    private final Set<Var> picks;

    private final Assignments assignments;
    private final AssignmentException exception;

    public FixedPicks(Set<Var> picks, Assignments assignments, AssignmentException exception) {
        Preconditions.checkArgument((assignments == null && exception != null) ||
                (assignments != null && exception == null));

//        this.pickRaw = pickRaw;
        this.picks = picks;
        this.assignments = assignments;
        this.exception = exception;
    }

    @Nonnull
    @Override
    public Object getKey() {
        return picks;
    }

    public FixedPicks(Assignments assignments) {
        this(null, assignments, null);
    }

    public FixedPicks(AssignmentException exception) {
        this(null, null, exception);
    }

    public FixedPicks(FixedPicks that) {
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

    public Set<Var> getPicks() {
        return picks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FixedPicks that = (FixedPicks) o;
        if (exception != null) {
            return picks.equals(that.picks) && exception.equals(that.exception);
        } else {
            return assignments.equals(that.assignments);
        }
    }

    @Override
    public int hashCode() {
        if (exception != null) {
            return 31 * picks.hashCode() + exception.hashCode();
        } else {
            return assignments.hashCode();
        }
    }


}
