package com.tms.threed.threedFramework.imageModel.shared;

import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedFramework.util.lang.shared.Path;

import java.util.Set;

public interface IPng {

    boolean isBlink();

    boolean isVisible();

    ILayer getLayer();

    Path getUrl(Path pngImageBase);

    Set<Var> getFeatures();
}
