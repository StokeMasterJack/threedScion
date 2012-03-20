package com.tms.threed.threedCore.imageModel.shared.slice;

import com.tms.threed.threedCore.featureModel.shared.boolExpr.*;
import com.tms.threed.threedCore.imageModel.shared.ILayer;
import com.tms.threed.threedCore.imageModel.shared.IPng;

import java.util.*;

public class Layer extends BaseNode implements LayerOrFeature, Child, Parent, ILayer {

    private final String name;    //ex: 01_DoorHandles

    private final FeatureOrPng[] childNodes;

    private boolean complex;

    private ImageSlice imageModel; //parent

    public Png currentPng;

    private boolean visible = true; //for testHarness

    public Layer(String name, List<FeatureOrPng> childNodes) {
        if (name == null) throw new IllegalArgumentException();
        if (childNodes == null) throw new IllegalArgumentException();

        this.childNodes = new FeatureOrPng[childNodes.size()];
        childNodes.toArray(this.childNodes);

        this.name = name;
    }


    public boolean isComplex() {
        return complex;
    }

//    private boolean initComplex() {
//        Collection<Png> pngsWithNephews = getPngsWithNephews();
//        return pngsWithNephews != null && pngsWithNephews.size() > 0;
//    }

    public void _initParent(ImageSlice imageModel) {
        this.imageModel = imageModel;
        for (int i = 0; i < childNodes.length; i++) {
            childNodes[i]._initParent(this, this);
        }
        this.complex = initComplex1();
//        System.out.println("layer[" + getSimpleName() + "].isComplex=" + isComplex());
//        System.out.println("layer[" + getSimpleName() + "].isBackground=" + isBackground());

    }

    @Override
    public Parent getParent() {
        return imageModel;
    }

    public ImageSlice getImageModel() {
        return imageModel;
    }

    public String getName() {
        return name;
    }

    public String getSimpleName() {
        return name;
    }

    public int getIndex() {
        String n = getSimpleName();
        String s = n.substring(0, 2);
        return Integer.parseInt(s);
    }

    public FeatureOrPng[] getChildNodes() {
        return childNodes;
    }

    @Override
    public Layer getLayer() {
        return this;
    }

    public boolean isFeatureless() {
        for (FeatureOrPng child : childNodes) {
            if (child.isFeature()) return false;
        }
        return true;
    }

    public void setCurrentPng(Png newPng) {
        if (this.currentPng == null) {
            this.currentPng = newPng;
        } else {
            if (newPng.featureCount > currentPng.featureCount) {
                currentPng = newPng;
            } else if (newPng.featureCount == currentPng.featureCount) {

                String msg;
                boolean error;

//                if (newPng.getShortSha().equals(currentPng.getShortSha())) {
//                    msg = "Png[" + newPng.getShortSha() + "] appears more thant once in png tree for layer[" + getSimpleName() + "]";
//                    error = false;
//                } else {
//                    msg = "Ambiguous png.featureCount on layer[" + getSimpleName() + "]";
//                    error = true;
//                }

//
//                System.err.println(msg);
//                System.err.println("\t" + newPng + ".featureCount: " + newPng.getFeatureCount());
//                System.err.println("\t" + newPng + ".features: " + newPng.getFeatures());
//
//                System.err.println("\t" + currentPng + ".featureCount: " + currentPng.getFeatureCount());
//                System.err.println("\t" + currentPng + ".features: " + currentPng.getFeatures());


            } else {
                //do nothing
            }
        }


    }

    public IPng computePngForPicks(SimplePicks ctx) {
        currentPng = null;
        for (int i = 0; i < childNodes.length; i++) {
            childNodes[i].maybePickPng(ctx);
            if (currentPng != null && !complex) {
                return currentPng;
            }
        }
        return currentPng;
    }

    @Override
    public IPng computePngForPicks(SimplePicks simplePicks, int angle) {
        return computePngForPicks(simplePicks);
    }

    /**
     * @return true if this layer represents an accessory
     */
    public boolean isAccessory() {
        String n = getSimpleName().toLowerCase();
        if (n.contains("_acc_")) return true;
        if (n.contains("-acc-")) return true;
        if (n.contains("_acc-")) return true;
        if (n.contains("-acc_")) return true;
        if (n.contains("_zacc")) return true;
        if (n.contains("-zacc")) return true;
        return false;
    }

//    public boolean isBackgroundLayer() {
//        return this.childNodes.length == 1 && this.childNodes[0].isPng();
//    }
//
//    public Png getBackgroundPng() {
//        assert isBackgroundLayer();
//        return this.childNodes[0].asPng();
//    }


    public boolean isZLayer() {
        String n = getSimpleName();
        return n.charAt(3) == 'z';
    }

    public boolean isJpgLayer() {
        return !isZLayer();
    }

    public boolean isMandatoryLayer() {
        String n = getSimpleName();
        return n.endsWith("_M");
    }

    public boolean isPartOfJpg() {
        return !isZLayer();
    }

    public boolean isBlinkPngRequired() {
        return isAccessory();
    }

    public void getVars(Set<Var> varSet) {
        for (int i = 0; i < childNodes.length; i++) {
            FeatureOrPng featureOrPng = childNodes[i];
            featureOrPng.getVars(varSet);
        }
    }

    public void getPngs(Set<Png> pngs) {
        for (FeatureOrPng fp : childNodes) {
            fp.getPngs(pngs);
        }
    }

    public Set<Png> getPngs() {
        HashSet<Png> a = new HashSet<Png>();
        getPngs(a);
        return a;
    }


//    public Layer simplify(Collection<Var> varToExclude) {
//        ArrayList<FeatureOrPng> a = new ArrayList<FeatureOrPng>();
//        for (FeatureOrPng childNode : childNodes) {
//            FeatureOrPng simple = childNode.simplify(varToExclude);
//            if (simple != null) {
//                a.add(simple);
//            }
//        }
//        if (a.size() == 0) return null;
//        return new Layer(this.name, a);
//    }

    public Collection<Png> getPngsWithNephews() {
        Collection<Png> retVal = new HashSet<Png>();

        for (Png png : getPngs()) {
            Collection<Png> nephews = png.getNephews();
            if (nephews.size() > 1) retVal.add(png);
        }

        return retVal;
    }

    private boolean initComplex1() {
        for (Png png : getPngs()) {
            if (png.hasNephews()) return true;
        }
        return false;
    }

    public BoolExpr getConstraint() {
        LinkedHashSet<BoolExpr> a = new LinkedHashSet<BoolExpr>();
        for (Png png : getPngs()) {
            a.add(png.getImplicant());
        }
        if (isMandatoryLayer()) {
            return new Xor(a);
        } else {
            //optional layer
            return new And(Junction.getConflicts(a));
        }

    }


    public void print() {
        System.out.println("Layer[" + getSimpleName() + "]");
        for (FeatureOrPng childNode : childNodes) {
            childNode.print(1);
        }
    }

    @Override
    public String toString() {
        return getSimpleName();
    }

    public void toggleVisibility() {
        visible = !visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }
}
