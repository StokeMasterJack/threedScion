package com.tms.threed.threedFramework.imageModel.shared;

import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedFramework.imageModel.shared.slice.FeatureOrPng;
import com.tms.threed.threedFramework.imageModel.shared.slice.Layer;
import com.tms.threed.threedFramework.imageModel.shared.slice.SimplePicks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImLayer extends ImChildBase implements ImLayerOrFeature, IsChild, IsParent<ImFeatureOrPng>, Comparable<ImLayer>, ILayer {

    private final String name;
    private final ArrayList<ImFeatureOrPng> childNodes;

    private boolean visible = true; //for testHarness

    public ImLayer(int depth, String name, List<ImFeatureOrPng> childNodes) {
        super(depth);
        this.name = name;
        this.childNodes = (ArrayList<ImFeatureOrPng>) childNodes;

        for (ImFeatureOrPng node : childNodes) {
            node.initParent(this);
        }
    }

    @Override
    public IPng computePngForPicks(SimplePicks simplePicks, int angle) {
        return getPng(simplePicks, angle);
    }

    public String getName() {
        return name;
    }

    public boolean isVisible() {
        return visible;
    }

    @Override
    public String getSimpleName() {
        return name;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public List<ImFeatureOrPng> getChildNodes() {
        return childNodes;
    }

    @Override
    public boolean isLayer() {
        return true;
    }

    public boolean isFeatureless() {
        if (childNodes == null) return true;
        for (ImFeatureOrPng node : childNodes) {
            if (node.isFeature()) return false;
        }
        return true;
    }

    public ImPng getPng(SimplePicks picks, int angle) {
        assert picks != null;
        PngMatch bestMatch = new PngMatch();
        for (ImFeatureOrPng featureOrPng : childNodes) {
            featureOrPng.getMatchingPngs(bestMatch, picks, angle);
        }
        return bestMatch.getPng();
    }

    /**
     * @return true if this layer represents an accessory
     */
    public boolean isAccessory() {
        String n = getName().toLowerCase();
        if (n.contains("_acc_")) return true;
        if (n.contains("-acc-")) return true;
        if (n.contains("_acc-")) return true;
        if (n.contains("-acc_")) return true;
        if (n.contains("_zacc")) return true;
        if (n.contains("-zacc")) return true;
        return false;
    }

    public boolean isZLayer() {
        String n = getName();
        return n.charAt(3) == 'z';
    }

    public boolean isPartOfJpg() {
        return !isZLayer();
    }

    public boolean isBlinkPngRequired() {
        return isAccessory();
    }

    @Override
    public boolean containsAngle(int angle) {
        if (childNodes == null) return false;
        for (int i = 0; i < childNodes.size(); i++) {
            if (childNodes.get(i).containsAngle(angle)) return true;
        }
        return false;
    }

//    public ImLayer copy(int angle) {
//        if (!containsAngle(angle)) return null;
//        else return new ImLayer(this.depth, this.name, );
//    }

    public ImLayer copy(int angle) {
        ArrayList<ImFeatureOrPng> a;
        if (childNodes == null) {
            a = null;
        } else {
            a = new ArrayList<ImFeatureOrPng>();
            for (ImFeatureOrPng childNode : childNodes) {
                if (childNode.containsAngle(angle)) a.add(childNode.copy(angle));
            }
        }
        return new ImLayer(depth, name, a);
    }

    public Layer simplify(int angle) {
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
        return new Layer(name, a);
    }


    public void getVars(Set<Var> varSet) {
        for (int i = 0; i < childNodes.size(); i++) {
            ImFeatureOrPng featureOrPng = childNodes.get(i);
            featureOrPng.getVarSet(varSet);
        }
    }

    public Set<Var> getVars() {
        HashSet<Var> set = new HashSet<Var>();
        getVars(set);
        return set;
    }

    public void getPngs(Set<ImPng> pngs) {
        for (ImFeatureOrPng fp : childNodes) {
            fp.getPngs(pngs);
        }
    }

    public Set<ImPng> getPngs() {
        HashSet<ImPng> set = new HashSet<ImPng>();
        getPngs(set);
        return set;
    }

    public String getShortName() {
        String[] a = name.split("_");
        int L = a.length;
        return a[L - 1];
    }

    public void toggleVisibility() {
        visible = !visible;
    }

    @Override
    public int compareTo(ImLayer that) {
        return this.name.compareTo(that.name);
    }

    public ImView getView() {
        return (ImView) getParent();
    }

    public boolean isBackground() {
        return getName().contains("Background");
    }

    @Override public boolean equals(Object obj) {

        if (obj == null) return false;
        if (obj.getClass() != ImLayer.class) return false;

        ImLayer that = (ImLayer) obj;

        boolean nameEq = (name.equals(that.name));

        boolean childNodesEq = childNodes.containsAll(that.childNodes);


//        return nameEq;
        return nameEq && childNodesEq;
    }
}
