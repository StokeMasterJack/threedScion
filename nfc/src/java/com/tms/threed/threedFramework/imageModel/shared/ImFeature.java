package com.tms.threed.threedFramework.imageModel.shared;

import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedFramework.imageModel.shared.slice.Feature;
import com.tms.threed.threedFramework.imageModel.shared.slice.FeatureOrPng;
import com.tms.threed.threedFramework.imageModel.shared.slice.SimplePicks;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ImFeature extends ImChildBase implements ImFeatureOrPng, ImLayerOrFeature, IsChild, IsParent<ImFeatureOrPng> {

    private final Var var;
    private final List<ImFeatureOrPng> childNodes;

    public ImFeature(int depth, @Nonnull Var var, List<ImFeatureOrPng> childNodes) {
        super(depth);
        if (var == null) throw new IllegalArgumentException("var must be non-null");
        if (childNodes == null) throw new IllegalArgumentException("childNodes must be non-null");
        this.var = var;
        this.childNodes = childNodes;

        for (ImFeatureOrPng node : childNodes) {
            node.initParent(this);
        }
    }

    @Override
    public String getName() {
        return var.getCode();
    }

    public Var getVar() {
        return var;
    }

    public boolean is(Var var) {
        return this.var == var;
    }

    public List<ImFeatureOrPng> getChildNodes() {
        return childNodes;
    }


    @Override
    public boolean isFeature() {
        return true;
    }

    @Override
    public boolean isPng() {
        return false;
    }

    @Override
    public boolean containsAngle(int angle) {
        if (childNodes == null) return false;
        for (int i = 0; i < childNodes.size(); i++) {
            if (childNodes.get(i).containsAngle(angle)) return true;
        }
        return false;
    }

    @Override
    public void getMatchingPngs(PngMatch bestMatch, SimplePicks picks, int angle) {
        assert picks != null : "picks must be non-null";
        assert bestMatch != null : "bestMatch must be non-null";
        assert var != null : "var must be non-null before calling getMatchingPngs";

        boolean varPicked = picks.isPicked(var);

        if (!varPicked) return;

        for (ImFeatureOrPng featureOrPng : childNodes) {
            if (featureOrPng == null) throw new IllegalStateException("childNodes should not be null");
            featureOrPng.getMatchingPngs(bestMatch, picks, angle);
        }
    }


    public boolean isLayer() {
        return false;
    }

    public ImFeature copy(int angle) {
        ArrayList<ImFeatureOrPng> a = new ArrayList<ImFeatureOrPng>();
        for (ImFeatureOrPng childNode : childNodes) {
            if (childNode.containsAngle(angle)) a.add(childNode.copy(angle));
        }
        return new ImFeature(depth, var, a);
    }

    public FeatureOrPng simplify(int angle) {

        ArrayList<FeatureOrPng> a = new ArrayList<FeatureOrPng>();

        for (ImFeatureOrPng childNode : childNodes) {
            if (childNode.containsAngle(angle)) {
                FeatureOrPng simple = childNode.simplify(angle);
                if (simple != null) {
                    a.add(simple);
                }
            }
        }

        if (a.size() == 0) return null;

//        if (a.size() == 1) {
//            return new SimpleFeature(var, a.get(0));
//        } else {
        return new Feature(var, a);
//        }

    }

    @Override
    public void getVarSet(Set<Var> varSet) {
        varSet.add(var);
        for (int i = 0; i < childNodes.size(); i++) {
            ImFeatureOrPng featureOrPng = childNodes.get(i);
            featureOrPng.getVarSet(varSet);
        }
    }

    @Override
    public void getPngs(Set<ImPng> pngs) {
        for (ImFeatureOrPng fp : childNodes) {
            fp.getPngs(pngs);
        }

    }

    @Override public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != ImFeature.class) return false;

        ImFeature that = (ImFeature) obj;

        boolean varEq = (var.equals(that.var));


        if (!varEq) return false;

        boolean childNodesEq = childNodes.containsAll(that.childNodes);


        if (childNodesEq) {
            return true;


        } else {

            System.out.println(childNodes);
            System.out.println(that.childNodes);
            System.out.println();

            return false;
        }


    }
}
