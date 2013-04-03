package c3i.imageModel.shared;


import java.util.Set;

public interface ImFeatureOrPng extends IsChild {

    void getMatchingPngs(PngMatch bestMatch, SimplePicks picks, int angle);

    ImFeatureOrPng copy(int angle);

    void getVarSet(Set<String> varSet);

    void getVarSet(Set<String> varSet, int angle);

    void getPngs(Set<SrcPng> pngs);

}
