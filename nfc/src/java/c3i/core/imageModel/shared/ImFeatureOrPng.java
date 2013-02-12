package c3i.core.imageModel.shared;


import java.util.Set;

public interface ImFeatureOrPng extends IsChild {

    void getMatchingPngs(PngMatch bestMatch, SimplePicks picks, int angle);

    ImFeatureOrPng copy(int angle);

    void getVarSet(Set<Object> varSet);

    void getVarSet(Set<Object> varSet, int angle);

    void getPngs(Set<SrcPng> pngs);


}
