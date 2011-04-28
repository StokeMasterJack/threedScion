package com.tms.threed.threedFramework.imageModel.shared;

import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedFramework.imageModel.shared.slice.ImageSlice;
import com.tms.threed.threedFramework.imageModel.shared.slice.Layer;
import com.tms.threed.threedFramework.imageModel.shared.slice.SimplePicks;
import com.tms.threed.threedFramework.repo.shared.JpgWidth;
import com.tms.threed.threedFramework.threedCore.shared.Slice;
import com.tms.threed.threedFramework.threedCore.shared.ViewKey;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImView extends ImChildBase implements IsParent<ImLayer> {

    private final String name;
    private final List<ImLayer> layers;

    transient private ViewKey viewKey;

    public ImView(int depth, String name, List<ImLayer> layers) {
        super(depth);

        if (name == null) throw new IllegalArgumentException();
        if (layers == null) throw new IllegalArgumentException();

        this.name = name;
        Collections.sort(layers);
        this.layers = layers;

        for (ImLayer layer : layers) {
            layer.initParent(this);
        }
    }

    @Override
    public void initParent(IsParent parent) {
        super.initParent(parent);
        viewKey = initViewKey();
    }

    private ViewKey initViewKey() {
        return ((ImSeries) parent).getSeriesInfo().getViewKeyByName(name);
    }

    public ViewKey getViewKey() {
        return viewKey;
    }

    @Override
    public boolean isView() {
        return true;
    }

    @Override
    public boolean containsAngle(int angle) {
        if (layers == null) return false;
        for (int i = 0; i < layers.size(); i++) {
            if (layers.get(i).containsAngle(angle)) return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<ImLayer> getLayers() {
        return layers;
    }

    public List<ImLayer> getLayers(int angle) {
        ArrayList<ImLayer> a = new ArrayList<ImLayer>();
        for (ImLayer layer : layers) {
            if (layer.containsAngle(angle)) {
                a.add(layer);
            }
        }
        return a;
    }

    public ImageStack getImageStack(SimplePicks picks, int angle, JpgWidth jpgWidth) {
        assert picks != null;
        assert angle > 0 && angle <= getAngleCount();

        ArrayList<ImPng> jPngs = new ArrayList<ImPng>();
        ArrayList<ImPng> zPngs = new ArrayList<ImPng>();

        for (ImLayer layer : layers) {
            ImPng png = layer.getPng(picks, angle);
            if (png != null) {
                if (layer.isZLayer()) {
                    zPngs.add(png);
                } else {
                    jPngs.add(png);
                }
            }
        }

        return new ImageStack(jPngs, zPngs, this, angle, jpgWidth);
    }

    public boolean is(ViewKey viewKey) {
        return this.viewKey.equals(viewKey);
    }

    public boolean is(String viewName) {
        return this.name.equalsIgnoreCase(viewName);
    }


//    public Jpg getJpg(Picks picks, int angle) {
//        final List<Png> pngs = getPngs(picks, angle);
//        if (pngs == null) return null;
//        int version = 1; //todo fix this: "version" shouldn't be hard-coded
//
//        assert pngs.size() != 0;
////        return new Jpg(this, pngs, angle);
//        return null;
//    }

    public int getLayerCount() {
        if (layers == null) return 0;
        return layers.size();
    }

    @Override
    public List<ImLayer> getChildNodes() {
        return layers;
    }

    @Nullable
    public ImPng getAccessoryPng(int angle, SimplePicks picks, Var accessory) {

        assert picks != null;
        assert accessory != null;

        ImageStack imageStack = this.getImageStack(picks, angle, JpgWidth.W_STD);


        List<ImPng> blinkPngs = imageStack.getBlinkPngs();

        class FeaturePng {
            ImPng png;
            int featureIndex;

            FeaturePng(ImPng png, int featureIndex) {
                this.png = png;
                this.featureIndex = featureIndex;
            }
        }

        FeaturePng bestMatch = null;

        for (ImPng blinkPng : blinkPngs) {
            int featureIndex = blinkPng.indexOf(accessory);
            if (featureIndex == -1) continue;

            if (bestMatch == null) {
                bestMatch = new FeaturePng(blinkPng, featureIndex);
            } else if (featureIndex < bestMatch.featureIndex) {
                bestMatch.featureIndex = featureIndex;
                bestMatch.png = blinkPng;
            }
        }

        if (bestMatch == null) {
            //accessory not visible for this angle
            return null;
        } else {
            return bestMatch.png;
        }

    }

    public ImView copy(int angle) {
        ArrayList<ImLayer> a;
        if (layers == null) {
            a = null;
        } else {
            a = new ArrayList<ImLayer>();
            for (ImLayer childNode : layers) {
                if (childNode.containsAngle(angle)) a.add(childNode.copy(angle));
            }
        }
        ImView copy = new ImView(depth, name, a);
        copy.parent = parent;
        return copy;
    }

    public Set<Var> getJpgVars() {
        HashSet<Var> vars = new HashSet<Var>();
        getJpgVars(vars);
        return vars;
    }

    public void getJpgVars(Set<Var> varSet) {
        for (int i = 0; i < layers.size(); i++) {
            ImLayer imLayer = layers.get(i);
            if (imLayer.isZLayer()) continue;
            imLayer.getVars(varSet);
        }
    }

    public Set<ImPng> getPngs() {
        HashSet<ImPng> set = new HashSet<ImPng>();
        populatePngSet(set);
        return set;
    }

    public void populatePngSet(Set<ImPng> pngs) {
        for (ImLayer imLayer : layers) {
            imLayer.getPngs(pngs);
        }
    }

    public String getVarCode() {
        return getName() + "View";
    }

    public int getAngleCount() {
        return getViewKey().getAngleCount();
    }

    /**
     * For use in TestHarness
     */
    public void selectAll() {
        for (ImLayer layer : layers) {
            layer.setVisible(true);
        }
    }

    /**
     * For use in TestHarness
     */
    public void selectNone() {
        for (ImLayer layer : layers) {
            layer.setVisible(false);
        }
    }

    public ImSeries getSeries() {
        return getParent().asSeries();
    }

    public String getSeriesName() {
        return getSeries().getName();
    }

    public ImJpg getJpg(SimplePicks picks, int angle, JpgWidth jpgWidth) {
        return getImageStack(picks, angle, jpgWidth).getJpg();
    }

    public ImageSlice createSlice(int angle) {
        ArrayList<Layer> layers = new ArrayList<Layer>();
        for (ImLayer layer : this.layers) {
            Layer sLayer = layer.simplify(angle);
            if (sLayer != null) {
                layers.add(sLayer);
            }
        }
        if (layers.size() == 0) throw new IllegalStateException();
        Slice slice = new Slice(getViewKey().getName(), angle);
        return new ImageSlice(slice, layers);
    }

    public void printSummary() {
        System.out.println("\t " + viewKey.getName() + "\tLayers[ " + layers.size() + "]\t vars[" + getJpgVars().size() + "]");
    }

    public Slice getSlice(int angle) {
        return new Slice(viewKey.getName(), angle);
    }

    @Override public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != ImView.class) return false;

        ImView that = (ImView) obj;

        boolean vkEq = (viewKey.equals(that.viewKey));

        boolean layersEq = (layers.equals(that.layers));

        return vkEq && layersEq;
    }
}
