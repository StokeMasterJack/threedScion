package c3i.imageModel.shared;


import c3i.featureModel.shared.boolExpr.Var;

import java.util.Set;

public interface ImFeatureOrPng extends IsChild {

    void getMatchingPngs(PngMatch bestMatch, SimplePicks picks, int angle);

    ImFeatureOrPng copy(int angle);

    void getVarSet(Set<Var> varSet);

    void getVarSet(Set<Var> varSet, int angle);

    void getPngs(Set<SrcPng> pngs);


}
