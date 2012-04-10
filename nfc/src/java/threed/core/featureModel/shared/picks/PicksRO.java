package threed.core.featureModel.shared.picks;

import threed.core.featureModel.shared.*;
import threed.core.featureModel.shared.boolExpr.Var;
import threed.core.imageModel.shared.slice.SimplePicks;

import java.util.Collection;
import java.util.Set;

public interface PicksRO extends EvalContext,SimplePicks {

    PicksContext getPicksContext();

    Bit getValue(Var var);

    Set<Var> getUnassignedVars();

    Set<Var> getVarsByValue(Bit filter);

    boolean anyAssignments();

    Set<Var> toVarSet(Bit value, Source source);

    Set<Var> getUserPicks();

    PicksKey getKey();

    int getUnassignedVarCount();

    int getAssignedVarCount();

    int getPickCount();

    void printPicks();

    void printAssignments(Bit filter);

    void printAssignments(Bit filter, Boolean leaf);

    void printAssignments(Boolean leaf);

    void printAssignments();

    void printUnassignedVars();

    Set<Var> getAllPicks();

    Set<String> getAllPicks2();

    boolean isAssigned(Var var);

    boolean isUnassigned(Var var);

    boolean isTrue(Var var);

    boolean isFalse(Var var);

    boolean isPicked(Var var);

    boolean isPicked(String code);

    boolean containsAll(Collection<String> features);

    boolean containsAllVars(Collection<Var> features);

    @Override String toString();

    @Override boolean equals(Object o);

    @Override int hashCode();

    PicksAssignment getAssignment(Var var);

    PicksSnapshot createSnapshot();

    Picks copyIgnoreFixupPicks();

    boolean isValid();

    String getErrorMessage();
}
