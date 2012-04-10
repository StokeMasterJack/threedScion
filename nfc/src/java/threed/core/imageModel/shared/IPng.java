package threed.core.imageModel.shared;

import threed.core.featureModel.shared.boolExpr.Var;
import smartsoft.util.lang.shared.Path;

import java.util.Set;

public interface IPng {

    boolean isBlink();

    boolean isVisible();

    ILayer getLayer();

    Path getUrl(Path pngImageBase);

    Set<Var> getFeatures();
}
