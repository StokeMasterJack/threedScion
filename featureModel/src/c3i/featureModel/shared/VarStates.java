package c3i.featureModel.shared;

import c3i.featureModel.shared.boolExpr.Var;
import c3i.featureModel.shared.node.Csp;

import java.util.LinkedHashSet;

public class VarStates {

    public static enum Filter {AllVars, OutputVarsOnly, NonOutputVarsOnly}

    private final Filter filter;

    private final LinkedHashSet<Var> trueVars = new LinkedHashSet<Var>();
    private final LinkedHashSet<Var> falseVars = new LinkedHashSet<Var>();
    private final LinkedHashSet<Var> openVars = new LinkedHashSet<Var>();


    public VarStates(Csp csp) {
        this(csp, Filter.AllVars);
    }

    public VarStates(Csp csp, Filter filter) {

        this.filter = filter;

        for (int i = 0; i < csp.getAllVars().size(); i++) {


            Var var = csp.getVar(i);

            boolean outputVar;

            outputVar = csp.isOutVar(var);

            if (isMatch(var, outputVar)) {
                Tri v = csp.getValue(var);
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

