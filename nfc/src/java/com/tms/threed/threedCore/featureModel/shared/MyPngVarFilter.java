package com.tms.threed.threedCore.featureModel.shared;

import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;

import java.util.Set;

public class MyPngVarFilter implements VarFilter {

    private final boolean[] isPngArray;
    private final int pngVarCount;

    public MyPngVarFilter(Vars vars, Set<Var> pngVars) {
        isPngArray = new boolean[vars.size()];

        this.pngVarCount = pngVars.size();

        for (int i = 0; i < isPngArray.length; i++) {
            Var var = vars.get(i);
            isPngArray[i] = pngVars.contains(var);
        }
    }


    @Override
    public boolean accept(Var var) {
        return isPngArray[var.index];
    }

    @Override
    public int getOutputVarCount() {
        return pngVarCount;
    }

}
