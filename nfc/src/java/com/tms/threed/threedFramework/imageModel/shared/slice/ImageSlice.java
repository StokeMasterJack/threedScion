package com.tms.threed.threedFramework.imageModel.shared.slice;

import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedFramework.threedCore.shared.Slice;

import java.util.*;

public class ImageSlice extends BaseNode implements Parent {

    private final Slice slice;

    private final Layer[] jpgLayers; //non-z and non-background
    private final Layer[] zLayers;

    public ImageSlice(Slice slice, List<Layer> layers) {
        assert slice != null;
        assert layers != null;

        this.slice = slice;

        ArrayList<Layer> jpgLayers = new ArrayList<Layer>();
        ArrayList<Layer> zLayers = new ArrayList<Layer>();

        for (int i = 0; i < layers.size(); i++) {
            Layer layer = layers.get(i);
            layer._initParent(this);
            if (layer.isZLayer()) {
                zLayers.add(layer);
            } else if (layer.isJpgLayer()) {
                jpgLayers.add(layer);
            } else {
                throw new IllegalStateException();
            }
        }

        this.jpgLayers = new Layer[jpgLayers.size()];
        jpgLayers.toArray(this.jpgLayers);

        this.zLayers = new Layer[zLayers.size()];
        zLayers.toArray(this.zLayers);


    }

    public Slice getSlice() {
        return slice;
    }

    @Override
    public Parent getParent() {
        return null;
    }

    @Override
    public String getSimpleName() {
        return "ImageModel";
    }

    public Set<Var> getJpgVars() {
        HashSet<Var> vars = new HashSet<Var>();
        getJpgVars(vars);
        return vars;
    }

    public void getJpgVars(Set<Var> vars) {
        for (Layer layer : jpgLayers) {
            layer.getVars(vars);
        }
    }

    public Set<Png> getNonZPngs() {
        HashSet<Png> pngs = new HashSet<Png>();
        this.getNonZPngs(pngs);
        return pngs;
    }

    public void getNonZPngs(Set<Png> pngs) {
        for (Layer layer : zLayers) {
            layer.getPngs(pngs);
        }
    }

    public String getVarCode() {
        return "imageModel";
    }

    public void printMiddleFeaturesReport() {
        Set<Var> aVarSet = getJpgVars();
        int aVarCount = aVarSet.size();
        System.out.println("\t\t :" + aVarCount + ": " + aVarSet);
    }


    public int getJpgLayerCount() {
        return jpgLayers.length;
    }

    public int getZLayerCount() {
        return zLayers.length;
    }


    public String summary() {
        return "jpgLayers[" + getJpgLayerCount() + "] \t zLayers[" + getZLayerCount() + "]";
    }

    public int getNonZVarCount() {
        return getJpgVars().size();
    }

    public Collection<Png> getNonZPngsWithNephews() {
        Collection<Png> retVal = new HashSet<Png>();

        for (Png png : getNonZPngs()) {
            Collection<Png> nephews = png.getNephews();
            if (nephews.size() > 1) retVal.add(png);
        }

        return retVal;
    }


    /**
     * Returns the jpg fingerprint: pngShortSha1-pngShortSha1-pngShortSha1-pngShortShaN
     * <p/>
     * Does *not* include any zLayer pngs
     */
    public Jpg computeJpg(SimplePicks ctx) {
        Jpg jpg = new Jpg(jpgLayers.length);

        for (int i = 0; i < jpgLayers.length; i++) {
            Layer layer = jpgLayers[i];

            layer.computePngForPicks(ctx);
            Png layerPng = layer.currentPng;
            if (layerPng != null) {
                jpg.add(layerPng);
            }
        }

        return jpg;
    }

    public List<Png> computeZPngs(SimplePicks ctx) {
        ArrayList<Png> zPngs = new ArrayList<Png>(zLayers.length);

        for (int i = 0; i < zLayers.length; i++) {
            Layer layer = zLayers[i];

            layer.computePngForPicks(ctx);
            Png layerPng = layer.currentPng;
            if (layerPng != null) {
                zPngs.add(layerPng);
            }
        }

        return zPngs;
    }

//    public ImgStack computeImgStack(SimplePicks ctx, Path imageBase) {
//        List<Png> zPngs = computeZPngs(ctx);
//        Jpg jpg = computeJpg(ctx);
//        return new ImgStack(jpg, zPngs, imageBase);
//    }

    public void print() {
        for (Layer jpgLayer : jpgLayers) {
            jpgLayer.print();
        }
        for (Layer zLayer : zLayers) {
            zLayer.print();
        }
    }

    public Layer[] getJpgLayers() {
        return jpgLayers;
    }

    public Layer[] getZLayers() {
        return zLayers;
    }

    public List<Layer> getAllLayers() {
        ArrayList<Layer> a = new ArrayList<Layer>();
        a.addAll(Arrays.asList(jpgLayers));
        a.addAll(Arrays.asList(zLayers));
        return a;
    }

    /**
     * For use in TestHarness
     */
    public void selectAll() {
        for (Layer layer : getAllLayers()) {
            layer.setVisible(true);
        }
    }

    /**
     * For use in TestHarness
     */
    public void selectNone() {
        for (Layer layer : getAllLayers()) {
            layer.setVisible(false);
        }
    }

}
