package com.tms.threed.threedCore.imageModel.shared;

import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedCore.imageModel.shared.slice.FeatureOrPng;
import com.tms.threed.threedCore.imageModel.shared.slice.SimplePicks;

import javax.annotation.Nullable;
import java.util.Set;

public interface ImFeatureOrPng extends IsChild {

    void getMatchingPngs(PngMatch bestMatch, SimplePicks picks, int angle);

    ImFeatureOrPng copy(int angle);

    /**
     *  returning null means node goes away
     */
    @Nullable FeatureOrPng simplify(int angle);

    void getVarSet(Set<Var> varSet);

    void getPngs(Set<ImPng> pngs);


}
