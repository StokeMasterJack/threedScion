package c3i.featureModel.shared;

import c3i.featureModel.shared.boolExpr.BoolExpr;
import c3i.featureModel.shared.boolExpr.Var;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RelatedVarFinder {

    private final Set<BoolExpr> allClauses;
    private final HashSet<Var> relatedVars = new HashSet<Var>();

    public RelatedVarFinder(Collection<BoolExpr> allClauses) {
        this.allClauses = Collections.unmodifiableSet(new HashSet<BoolExpr>(allClauses));
    }

    public Set<Var> getRelated(Var var) {
        relatedVars.clear();
        relatedVars.add(var);
        processWhileChanging();
        return relatedVars;
    }

    public Set<Var> getRelated(Collection<Var> vars) {
        relatedVars.clear();
        relatedVars.addAll(vars);
        processWhileChanging();
        return relatedVars;
    }

    public boolean isRelated(Var var1, Var var2) {
        Set<Var> related = getRelated(var1);
        return related.contains(var2);
    }

    private void processWhileChanging() {
        while (true) {
            boolean changed = process();
            if (!changed) break;
        }
    }


    private boolean process() {
        Set<Var> toBeAdded = new HashSet<Var>();
        for (Var var : relatedVars) {
            for (BoolExpr clause : allClauses) {
                if (clause.containsVar(var)) {
                    toBeAdded.addAll(clause.getCareVars());
                }
            }
        }
        return relatedVars.addAll(toBeAdded);
    }

}
