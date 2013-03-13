package c3i.imageModel.shared;


import c3i.featureModel.shared.common.SimplePicks;

import java.util.Set;

public interface ImFeatureOrPng<V> extends IsChild<V> {

    void getMatchingPngs(PngMatch bestMatch, SimplePicks picks, int angle);

    ImFeatureOrPng<V> copy(int angle);

    void getVarSet(Set<V> varSet);

    void getVarSet(Set<V> varSet, int angle);

    void getPngs(Set<SrcPng<V>> pngs);


}
