package c3i.imageModel.shared;


import c3i.featureModel.shared.boolExpr.Var;

import java.util.Set;

public interface ViewSlice {

    Slice getSlice();

    Set<Var> getPngVars();

    int getAngle();

}
