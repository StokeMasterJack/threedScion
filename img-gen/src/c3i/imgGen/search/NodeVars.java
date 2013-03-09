package c3i.imgGen.search;

public interface NodeVars<VAR_TYPE> {

    boolean isTrue(VAR_TYPE var);

    boolean isFalse(VAR_TYPE var);

    boolean isCare(VAR_TYPE var);

    boolean isDontCare(VAR_TYPE var);

    boolean isOutVar(VAR_TYPE var);

    VarSet<VAR_TYPE> getTrueVars();

    VarSet<VAR_TYPE> getFalseVars();

    VarSet<VAR_TYPE> getCareVars();

    VarSet<VAR_TYPE> getDontCares();

    VarSet<VAR_TYPE> getOutTrueVars();

    VarSet<VAR_TYPE> getOutFalseCares();

    VarSet<VAR_TYPE> getOutCareVars();

    VarSet<VAR_TYPE> getOutDontCares();
}
