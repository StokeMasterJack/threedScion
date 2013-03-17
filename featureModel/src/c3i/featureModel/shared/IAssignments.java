package c3i.featureModel.shared;

import c3i.featureModel.shared.boolExpr.AssignmentException;
import c3i.featureModel.shared.boolExpr.ConflictingAssignmentException;
import c3i.featureModel.shared.boolExpr.Var;
import c3i.featureModel.shared.common.SimplePicks;

import java.util.Set;

public interface IAssignments extends EvalContext, AutoAssignContext, SimplePicks {

    VarSpace getVars();

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

    boolean assign(Var var, boolean value) throws ConflictingAssignmentException;

    boolean assignTrue(Var var) throws ConflictingAssignmentException;

    boolean assignFalse(Var var) throws ConflictingAssignmentException;

    void dirty();

    void clean();

    Bit getValue(Var var);

    void dumpVars(String prefix);

    Set<Var> getOpenVars();

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


    Var getVar(int varIndex);

    boolean isSolved();

    boolean anyOpenVars();

    boolean isPicked(Var var);

    Var get(int i);

}
