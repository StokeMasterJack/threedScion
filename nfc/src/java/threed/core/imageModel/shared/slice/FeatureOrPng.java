package threed.core.imageModel.shared.slice;

import threed.core.featureModel.shared.boolExpr.Var;

import java.util.Collection;
import java.util.Set;

public interface FeatureOrPng extends Child {

    void _initParent(LayerOrFeature parent,Layer layer);

    void maybePickPng(SimplePicks picks);

    FeatureOrPng copy();

    Layer getLayer();

    void getVars(Set<Var> varSet);

    void getPngs(Set<Png> pngs);

    /**
     * Top-most feature is 0
     */
    int getDepth();

    LayerOrFeature getParent();

    FeatureOrPng simplify(Collection<Var> varToExclude);

    void print(int depth);
}
