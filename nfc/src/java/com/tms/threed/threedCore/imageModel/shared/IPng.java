package com.tms.threed.threedCore.imageModel.shared;

import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;
import smartsoft.util.lang.shared.Path;

import java.util.Set;

public interface IPng {

    boolean isBlink();

    boolean isVisible();

    ILayer getLayer();

    Path getUrl(Path pngImageBase);

    Set<Var> getFeatures();
}
