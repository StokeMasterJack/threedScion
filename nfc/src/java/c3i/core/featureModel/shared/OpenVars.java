package c3i.core.featureModel.shared;

import c3i.core.featureModel.shared.boolExpr.Var;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class OpenVars {

    @Nonnull
    private final List<Var> vars1;
    @Nonnull
    private final ArrayList<Var> openVars1;

    @Nullable
    private final List<Var> vars2;
    @Nullable
    private final ArrayList<Var> openVars2;

    public OpenVars(@Nonnull List<Var> vars1, @Nullable List<Var> vars2) {

        assert vars1 != null;
        assert vars1.size() > 0;

        this.vars1 = vars1;
        this.vars2 = vars2;

        openVars1 = new ArrayList<Var>(vars1);

        if (vars2 == null || vars2.size() == 0) {
            openVars2 = null;
        } else {
            openVars2 = new ArrayList<Var>(vars2);
        }
    }

    public OpenVars(@Nonnull List<Var> vars1) {
        this(vars1, null);
    }

    public OpenVars(OpenVars that) {

        //shallow copy vars1 and vars2
        this.vars1 = that.vars1;
        this.vars2 = that.vars2;


        //shallow copy openVars1 and openVars2
        openVars1 = new ArrayList<Var>(that.openVars1);

        if (that.openVars2 == null) {
            this.openVars2 = null;
        } else {
            openVars2 = new ArrayList<Var>(that.openVars2);
        }

    }

    public List<Var> getVars1() {
        return vars1;
    }

    public List<Var> getVars2() {
        return vars2;
    }

    public Var getNextDecisionVar() {
        if (anyOpenVars1()) {
            return openVars1.get(0);
        } else if (anyOpenVars2()) {
            return openVars2.get(0);
        } else {
            return null;
        }
    }


    public boolean remove(Var var) {
        return openVars1.remove(var) || openVars2.remove(var);
    }


    public OpenVars copy() {
        return new OpenVars(this);
    }


    public int getOpenOutputVarCount() {
        return openVars1.size();
    }


    public List<Var> getOpenVars1() {
        return openVars1;
    }


    public List<Var> getOpenVars2() {
        return openVars2;
    }


    public boolean isSolved1() {
        return openVars1.isEmpty();
    }


    public boolean isSolved2() {
        return openVars2.isEmpty();
    }


    public boolean isSolved() {
        return isSolved1() && isSolved2();
    }


    public boolean anyOpenVars1() {
        return !openVars1.isEmpty();
    }


    public boolean anyOpenVars2() {
        return !openVars2.isEmpty();
    }


    public boolean isOutputVar(Var var) {
        return vars1.contains(var);
    }
}
