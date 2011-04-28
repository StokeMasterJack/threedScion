package com.tms.threed.threedFramework.imageModel.shared.slice;

import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedFramework.util.lang.shared.Strings;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class Feature extends BaseNode implements FeatureOrPng, LayerOrFeature {

    private final Var var;
    private final FeatureOrPng[] childNodes;

    private LayerOrFeature parent;
    private Layer layer;

    public Feature(@Nonnull Var var, List<FeatureOrPng> childNodes) {
        this.var = var;

        this.childNodes = new FeatureOrPng[childNodes.size()];
        childNodes.toArray(this.childNodes);

    }

    @Override
    public void _initParent(LayerOrFeature parent, Layer layer) {
        this.parent = parent;
        this.layer = layer;

        for (FeatureOrPng childNode : childNodes) {
            childNode._initParent(this, layer);
        }
    }

    public LayerOrFeature getParent() {
        return parent;
    }

    @Override
    public Layer getLayer() {
        return layer;
    }

    @Override
    public String getSimpleName() {
        return var.getCode();
    }

    public Var getVar() {
        return var;
    }

    public boolean is(Var var) {
        return this.var == var;
    }

    public FeatureOrPng[] getChildNodes() {
        return childNodes;
    }

    @Override
    public void maybePickPng(SimplePicks picks) {
        if (picks.isPicked(var)) {
            for (int i = 0; i < childNodes.length; i++) {
                childNodes[i].maybePickPng(picks);
            }
        }
    }


    public boolean isLayer() {
        return false;
    }

    public Feature copy() {
        ArrayList<FeatureOrPng> a = new ArrayList<FeatureOrPng>();
        for (FeatureOrPng childNode : childNodes) {
            a.add(childNode.copy());
        }
        return new Feature(var, a);
    }

    @Override
    public void getVars(Set<Var> varSet) {
        varSet.add(var);
        for (int i = 0; i < childNodes.length; i++) {
            FeatureOrPng featureOrPng = childNodes[i];
            featureOrPng.getVars(varSet);
        }
    }

    @Override
    public void getPngs(Set<Png> pngs) {
        for (FeatureOrPng fp : childNodes) {
            fp.getPngs(pngs);
        }
    }

    @Override
    public FeatureOrPng simplify(Collection<Var> varsToExclude) {
        if (varsToExclude.contains(this.var)) {
            return null;
        } else {
            ArrayList<FeatureOrPng> a = new ArrayList<FeatureOrPng>();
            for (FeatureOrPng childNode : childNodes) {
                FeatureOrPng simple = childNode.simplify(varsToExclude);
                if (simple != null) {
                    a.add(simple);
                }
            }
            if (a.size() == 0) return null;
            return new Feature(this.var, a);
        }
    }

    @Override
    public void print(int depth) {
        System.out.println(Strings.indent(depth) + "Feature[" + getVar().getCode() + "]");
        for (FeatureOrPng childNode : childNodes) {
            childNode.print(depth + 1);
        }
    }

    @Override
    public String toString() {
        return "F[" + getVar().getCode() + "]";
    }

}
