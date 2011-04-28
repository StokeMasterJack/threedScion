package com.tms.threed.threedFramework.featureModel.shared.picks;

import com.tms.threed.threedFramework.featureModel.shared.Bit;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;

import java.util.List;
import java.util.Set;

public interface PicksMutable extends PicksRO {

    public void resetDirtyFlag();

    public void resetAllAssignments();

    public boolean autoAssign(Var var, boolean newValue);

    public void pick(Var var);

    public void userAssign(Var var, Bit newValue);

    public void userAssign(Var var, boolean value);

    public void userPick(String varCode);

    public void pick(Set<String> vars);

    public void userAssign(String varCode, boolean value);

    public void userAssign(String code, Bit value);

    public void userAssign(List<Var> vars);

    public void pick(Var... vars);

    public void pick(String... vars);

    public void fixup();

    public void fixupLeafVarsBasedOnDefaults();

    public void initVisibleDefaults();

    public void fixupNonLeafVarsBasedOnDefaults();

    public void unpick(Var var);

    public void parseAndPick(String commaDelimitedList);




}
