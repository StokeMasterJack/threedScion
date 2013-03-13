package c3i.imageModel.shared;

import c3i.featureModel.shared.common.SimplePicks;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImLayer<V> extends ImChildBase<V> implements ImLayerOrFeature<V>, IsChild<V>, IsParent<ImFeatureOrPng<V>, V>, Comparable<ImLayer<V>> {

    private final String name;
    private final ArrayList<ImFeatureOrPng<V>> childNodes;

    private final boolean liftLayer;

    public ImLayer(int depth, String name, List<ImFeatureOrPng<V>> childNodes, boolean liftLayer) {
        super(depth);
        this.name = name;
        this.childNodes = (ArrayList<ImFeatureOrPng<V>>) childNodes;

        this.liftLayer = liftLayer;
        for (ImFeatureOrPng node : childNodes) {
            node.initParent(this);
        }


    }

    public int getMaxAngle() {
        return getMaxAngle(this.childNodes);
    }

    private static <V> int getMaxAngle(List<ImFeatureOrPng<V>> childNodes) {
        int maxAngle = 0;
        for (ImFeatureOrPng childNode : childNodes) {
            int angle;
            if (childNode.isPng()) {
                SrcPng png = childNode.asPng();
                angle = png.getAngle();
            } else {
                ImFeature feature = childNode.asFeature();
                angle = getMaxAngle(feature.getChildNodes());
            }
            if (angle > maxAngle) {
                maxAngle = angle;
            }
        }
        return maxAngle;
    }

    @Override
    public void initParent(IsParent parent) {
        super.initParent(parent);
    }


    public int getDeltaY(SimplePicks picks, int angle) {
        if (!liftLayer) {
            return 0;
        }
        ImView view = getView();
        ViewLiftSpec liftSpec = view.getLiftSpec();
        Object triggerFeature = liftSpec.getTriggerFeature();
        if (picks.isPicked(triggerFeature)) {
            return liftSpec.getDeltaY();
        } else {
            return 0;
        }
    }

    public String getName() {
        return name;
    }

    public String getSimpleName() {
        return name;
    }

    public List<ImFeatureOrPng<V>> getChildNodes() {
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

    public PngSpec getPngSpec(SimplePicks picks, int angle) {
        SrcPng srcPng = getSrcPng(picks, angle);
        if (srcPng == null) {
            return null;
        }
        int deltaY = getDeltaY(picks, angle);
        return new PngSpec(srcPng, deltaY);
    }

    public SrcPng getSrcPng(SimplePicks picks, int angle) {
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
        return new ImLayer(depth, name, a, liftLayer);
    }

    public ImmutableList<LayerSlice> getLayerSlices() {
        throw new UnsupportedOperationException();
//        if (layerSlices == null) {
//            ImView view = getView();
//            int angleCount = view.getAngleCount();
//            ImmutableList.Builder<LayerSlice> builder = ImmutableList.builder();
//            for (int angle = 1; angle <= angleCount; angle++) {
//                builder.add(new LayerSlice2(this, angle));
//            }
//        }
//        return layerSlices;
    }

    public LayerSlice getLayerSlice(int angle) {
        ImmutableList<LayerSlice> layerSlices = getLayerSlices();
        LayerSlice layerSlice = layerSlices.get(angle - 1);
        assert layerSlice.getAngle() == angle;
        return layerSlice;
    }


    public void getVars(Set<V> varSet) {
        for (int i = 0; i < childNodes.size(); i++) {
            ImFeatureOrPng featureOrPng = childNodes.get(i);
            featureOrPng.getVarSet(varSet);
        }
    }

    public void getVars(Set<V> varSet, int angle) {
        for (int i = 0; i < childNodes.size(); i++) {
            ImFeatureOrPng<V> featureOrPng = childNodes.get(i);
            featureOrPng.getVarSet(varSet, angle);
        }
    }

    public Set<V> getVars() {
        HashSet<V> set = new HashSet<V>();
        getVars(set);
        return set;
    }

    public void getPngs(Set<SrcPng<V>> pngs) {
        for (ImFeatureOrPng<V> fp : childNodes) {
            fp.getPngs(pngs);
        }
    }

    public Set<SrcPng<V>> getPngs() {
        HashSet<SrcPng<V>> set = new HashSet<SrcPng<V>>();
        getPngs(set);
        return set;
    }

    public String getShortName() {
        String[] a = name.split("_");
        int L = a.length;
        return a[L - 1];
    }

    @Override
    public int compareTo(ImLayer that) {
        return this.name.compareTo(that.name);
    }

    public ImView<V> getView() {
        return (ImView<V>) getParent();
    }

    public boolean isBackground() {
        return getName().contains("Background");
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) return false;
        if (obj.getClass() != ImLayer.class) return false;

        ImLayer that = (ImLayer) obj;

        boolean nameEq = (name.equals(that.name));

        boolean childNodesEq = childNodes.containsAll(that.childNodes);


//        return nameEq;
        return nameEq && childNodesEq;
    }

    public boolean isLiftLayer() {
        return liftLayer;
    }
}
