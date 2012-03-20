package com.tms.threed.threedCore.featureModel.shared;

import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AssignmentsForTreeSearch extends AbstractAssignments<AssignmentsForTreeSearch> {

    private final OpenVars openVars;

    public AssignmentsForTreeSearch(Vars vars, OpenVars openVars) {
        super(vars);
        this.openVars = openVars;
    }

    public AssignmentsForTreeSearch(AssignmentsForTreeSearch that,OpenVars newOpenVarsPointer) {
        super(that);

        //shallow-copy, csp does the deep copy
        this.openVars = newOpenVarsPointer;
    }

    public AssignmentsForTreeSearch copy(OpenVars newOpenVarsPointer){
        return new AssignmentsForTreeSearch(this,newOpenVarsPointer);
    }

    public List<Var> getVars1() {
        return openVars.getOpenVars1();
    }

    public List<Var> getVars2() {
        return openVars.getOpenVars2();
    }


    protected boolean removeFromOpenVars(Var var) {
        return openVars.remove(var);
    }

    public boolean isOutputVar(Var var) {
        return openVars.isOutputVar(var);
    }

    public Set<Var> getTrueOutputVars() {
        final LinkedHashSet<Var> set = new LinkedHashSet<Var>();
        for (int i = 0; i < allVars.size(); i++) {
            Var var = allVars.get(i);
            Bit val = assignments[i];
            if (val.isTrue() && isOutputVar(var) && var.isLeaf()) {
                set.add(allVars.get(i));
            }
        }
        return set;
    }

    public int getOpenOutputVarCount() {
        return openVars.getOpenOutputVarCount();
    }

    public List<Var> getOpenVars1() {
        return openVars.getOpenVars1();
    }

    public List<Var> getOpenVars2() {
        return openVars.getOpenVars2();
    }

    public boolean isSolved1() {
        return openVars.isSolved1();
    }

    public boolean isSolved2() {
        return openVars.isSolved2();
    }

    public boolean isSolved() {
        return openVars.isSolved();
    }

    public boolean anyOpenVars1() {
        return openVars.anyOpenVars1();
    }

    public boolean anyOpenVars2() {
        return openVars.anyOpenVars2();
    }

    public OpenVars getOpenVars() {
        return openVars;
    }
}
