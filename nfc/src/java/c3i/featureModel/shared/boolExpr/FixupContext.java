package c3i.featureModel.shared.boolExpr;

import java.util.LinkedHashSet;
import java.util.Set;

public interface FixupContext {

    boolean isPng(Var var);

    void addFixupVar(Var var, LinkedHashSet<Imp> simpleImpls);

    boolean addIfClauseShouldBeKept(BoolExpr clause, Set<BoolExpr> set);

    void addFixupVarIfAppropriate(Var var, MasterConstraint constraint);
}
