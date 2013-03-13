package c3i.featureModel.shared;

import c3i.featureModel.shared.boolExpr.Var;

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
        return isPngArray[var.getIndex()];
    }

    @Override
    public int getOutputVarCount() {
        return pngVarCount;
    }

}
