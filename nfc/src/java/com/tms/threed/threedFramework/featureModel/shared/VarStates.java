package com.tms.threed.threedFramework.featureModel.shared;

import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;

import java.util.LinkedHashSet;

public class VarStates {

    public static enum Filter {AllVars, OutputVarsOnly, NonOutputVarsOnly}

    private final Filter filter;

    private final LinkedHashSet<Var> trueVars = new LinkedHashSet<Var>();
    private final LinkedHashSet<Var> falseVars = new LinkedHashSet<Var>();
    private final LinkedHashSet<Var> openVars = new LinkedHashSet<Var>();


    public VarStates(Assignments assignments) {
        this(assignments, Filter.AllVars);

    }

    public VarStates(Assignments assignments, Filter filter) {

        this.filter = filter;

        for (int i = 0; i < assignments.getVars().size(); i++) {


            Var var = assignments.get(i);

            boolean outputVar;

            if (assignments instanceof AssignmentsForTreeSearch) {
                AssignmentsForTreeSearch a = (AssignmentsForTreeSearch) assignments;
                outputVar = a.isOutputVar(var);
            } else {
                outputVar = true;
            }

            if (isMatch(var, outputVar)) {
                Tri v = assignments.getValue(var);
                if (v.isTrue()) trueVars.add(var);
                else if (v.isFalse()) falseVars.add(var);
                else if (v.isOpen()) openVars.add(var);
                else throw new IllegalStateException();

            }
        }

    }

    private boolean isMatch(Var var, boolean isOutputVar) {
        switch (filter) {
            case AllVars:
                return true;
            case OutputVarsOnly:
                return isOutputVar;
            case NonOutputVarsOnly:
                return !isOutputVar;
        }
        throw new IllegalStateException();
    }

    public void print(String prefix) {
        System.err.println(prefix + "trueVars: " + trueVars.size() + ":" + trueVars);
        System.err.println(prefix + "falseVars: " + falseVars.size() + ":" + falseVars);
        System.err.println(prefix + "openVars: " + openVars.size() + ":" + openVars);
    }

    public LinkedHashSet<Var> getTrueVars() {
        return trueVars;
    }

    public LinkedHashSet<Var> getFalseVars() {
        return falseVars;
    }

    public LinkedHashSet<Var> getOpenVars() {
        return openVars;
    }

    @Override
    public String toString() {
        return
                "\n\t  true: " + trueVars +
                        "\n\t false: " + falseVars +
                        "\n\t  open: " + openVars;
    }

}

