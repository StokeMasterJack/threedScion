package c3i.featureModel.shared;

import c3i.featureModel.shared.boolExpr.AssignmentException;
import c3i.featureModel.shared.boolExpr.Var;
import c3i.featureModel.shared.common.SimplePicks2;
//import c3i.util.shared.futures.HasKey;
import c3i.util.shared.futures.HasKey;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Set;

@Immutable
public class FixedPicks implements SimplePicks2, HasKey {

    private final Set<Var> picks;

    private final IAssignments IAssignments;
    private final AssignmentException exception;

    public FixedPicks(Set<Var> picks, IAssignments IAssignments, AssignmentException exception) {
        Preconditions.checkArgument((IAssignments == null && exception != null) ||
                (IAssignments != null && exception == null));

//        this.pickRaw = pickRaw;
        this.picks = picks;
        this.IAssignments = IAssignments;
        this.exception = exception;
    }

    @Nonnull
    @Override
    public Object getKey() {
        return picks;
    }

    public FixedPicks(IAssignments IAssignments) {
        this(null, IAssignments, null);
    }

    public FixedPicks(AssignmentException exception) {
        this(null, null, exception);
    }

    public FixedPicks(FixedPicks that) {
        this.picks = that.picks;
        this.IAssignments = that.IAssignments;
        this.exception = that.exception;
    }

    public IAssignments getIAssignments() {
        return IAssignments;
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
        assert IAssignments != null;
        return IAssignments.isPicked(var);
    }

    public boolean isValidBuild() {
        if (exception == null && IAssignments != null) {
            return true;
        } else if (exception != null && IAssignments == null) {
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
        if (isValidBuild()) return "Valid build: " + IAssignments.getTrueVars();
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
            return IAssignments.equals(that.IAssignments);
        }
    }

    @Override
    public int hashCode() {
        if (exception != null) {
            return 31 * picks.hashCode() + exception.hashCode();
        } else {
            return IAssignments.hashCode();
        }
    }


}
