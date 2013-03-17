package c3i.featureModel.shared.vars;

import c3i.featureModel.shared.VarSpace;
import c3i.featureModel.shared.boolExpr.Var;

import java.util.Iterator;

/**
 * when copying an Assignments object (as in descending a search tree) the AssignmentsContext should be shallow-copied
 */
public class AssignmentsContext implements VarSpace {

    private final VarSpace varSpace;

    protected AssignmentsContext(VarSpace varSpace) {
        this.varSpace = varSpace;
    }


    public VarSpace getVarSpace() {
        return varSpace;
    }

    public int size() {
        return varSpace.size();
    }

    public Var getVar(int varIndex) {
        return varSpace.getVar(varIndex);
    }

    public Var getVar(String varCode) {
        return varSpace.getVar(varCode);
    }

    @Override
    public boolean containsCode(String varCode) {
        return varSpace.containsCode(varCode);
    }

    @Override
    public boolean containsIndex(int varIndex) {
        return false;
    }

    @Override
    public Iterator<Var> iterator() {
        return varSpace.iterator();
    }
}


