package c3i.featureModel.shared;

import c3i.featureModel.shared.boolExpr.AssignmentException;
import c3i.featureModel.shared.boolExpr.Var;
import c3i.imageModel.shared.SimplePicks;

import java.util.Set;

public interface Assignments<A extends Assignments> extends EvalContext, AutoAssignContext, SimplePicks {

    Vars getVars();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    boolean isTrue(Var var);

    boolean isTrue(int varIndex);

    boolean isFalse(Var var);

    boolean isFalse(int varIndex);

    boolean isOpen(Var var);

    boolean isOpen(int varIndex);

    boolean isAssigned(Var var);

    void logAssignTrue(Var var, int depth);

    void logAssignFalse(Var var, int depth);

    @Override
    void assignTrue(Var var) throws AssignmentException;

    @Override
    void assignFalse(Var var) throws AssignmentException;

    @Override
    void assignTrue(Var var, int depth) throws AssignmentException;

    @Override
    void assignFalse(Var var, int depth) throws AssignmentException;

    void dirty();

    void clean();

    Bit getValue(Var var);

    void dumpVars(String prefix);

    Set<Var> getVarsWithValue(Bit val);

    Set<Var> getTrueVars();

    void fillInInitialPicks();

    Set<Var> getFalseVars();

    //    public Set<Var> getTrueOutputVars(Set<Var> varsToSuppressInSolution) {
//        final LinkedHashSet<Var> set = new LinkedHashSet<Var>();
//
//        for (int i = 0; i < vars.getVarCount(); i++) {
//
//            Var var = vars.get(i);
//            if (outputFilter != null && !outputFilter.accept(var)) continue;
//            if (varsToSuppressInSolution.contains(var)) continue;
//
//            Bit val = varStates[i].getValue();
//            if (!val.isTrue()) continue;
//
//            set.add(vars.get(i));
//        }
//        return set;
//    }
//
    VarStates snapVarsStates(VarStates.Filter filter);

    VarStates snapVarsStates();

    boolean isDirty();

    Var getFirstUnassignedXorVar();

    Var getVar(int varIndex);

    boolean isSolved();

    boolean anyOpenVars();

    boolean isPicked(Var var);

    Var get(int i);

}
