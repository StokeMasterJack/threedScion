package com.tms.threed.threedCore.featureModel.shared.search;

import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;

public class Assignment {

    private final Var var;
    private final boolean val;

    public Assignment(Var var, boolean val) {
        this.var = var;
        this.val = val;
    }

    public Var getVar() {
        return var;
    }

    public boolean getValue() {
        return val;
    }

}