package com.tms.threed.threedCore.imageModel.shared.slice;

import com.tms.threed.threedCore.featureModel.shared.boolExpr.And;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.BoolExpr;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.Not;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedCore.imageModel.shared.IPng;
import smartsoft.util.lang.shared.Path;
import smartsoft.util.lang.shared.Strings;

import java.util.*;

public class Png extends BaseNode implements FeatureOrPng, IPng {

    private final String shortSha;
    private LayerOrFeature parent;
    public int featureCount;
    private Layer layer;

    private boolean visible = true; //for testHarness

    private boolean blink;

    public Png(String shortSha, boolean blink) {
        this.shortSha = shortSha;
        this.blink = blink;
    }

    public void _initParent(LayerOrFeature parent, Layer layer) {
        this.parent = parent;
        this.layer = layer;

        this.featureCount = initFeatureCount();
    }

    public LayerOrFeature getParent() {
        assert (this.parent != this);
        return parent;
    }

    @Override
    public Layer getLayer() {
        return layer;
    }

    @Override
    public void maybePickPng(SimplePicks ctx) {
        layer.setCurrentPng(this);
    }

    public int indexOf(Var accessory) {
        Parent p = getParent();
        while (p.isFeature()) {
            Feature feature = (Feature) p;
            if (feature.is(accessory)) {
                return feature.getDepth();
            } else {
                p = feature.getParent();
            }
        }
        return -1;
    }

    public boolean hasFeature(Var accessory, ParentMap parentMap) {
        Parent p = parentMap.getParentOf(this);
        while (p.isFeature()) {
            Feature feature = (Feature) p;
            if (feature.is(accessory)) return true;
            p = parentMap.getParentOf(feature);
        }

        return false;
    }

    public Set<Var> getFeatures() {
        HashSet<Var> vars = new HashSet<Var>();
        getFeatures(vars);
        return vars;
    }

    public void getFeatures(Set<Var> features) {
        Parent p = parent;
        while (p.isFeature()) {
            Feature f = (Feature) p;
            features.add(f.getVar());
            p = p.asFeature().getParent();
        }
    }

    private int initFeatureCount() {
        int fc = 0;
        Parent p = this.parent;
        while (p.isFeature()) {
            fc++;
            p = p.getParent();
        }
        return fc;
    }

    public int getFeatureCount() {
        return featureCount;
    }

    @Override
    public void getVars(Set<Var> varSet) {
        //intentionally blank   //todo huh??
    }

    @Override
    public void getPngs(Set<Png> pngs) {
        pngs.add(this);
    }

    public Layer getLayer(ParentMap parentMap) {
        Node n = this;
        while (true) {
            n = parentMap.getParentOf(n);
            if (n.isLayer()) return n.asLayer();
        }
    }

    public Path getLayerRelativePath(ParentMap parentMap) {
        Node n = this;
        Path path = new Path(getShortSha());
        while (true) {
            n = parentMap.getParentOf(n);
            if (n.isLayer()) {
                return path;
            } else {
                path = path.prepend(n.getSimpleName());
            }
        }
    }

    public String getShortSha() {
        return shortSha;
    }

    @Override
    public String getSimpleName() {
        return getShortSha();
    }

    @Override
    public FeatureOrPng copy() {
        return this;
    }

    @Override
    public FeatureOrPng simplify(Collection<Var> varsToExclude) {
        return this;
    }

    @Override
    public void print(int depth) {
        System.out.println(Strings.indent(depth) + "Png[" + shortSha + "]");
    }

    public Collection<Png> getNephews() {
        Collection<Png> nephews = new HashSet<Png>();
        for (Png p : getLayer().getPngs()) {
            if (p.isNephewOf(this)) {
                nephews.add(p);
            }
        }
        return nephews;
    }

    public boolean hasNephews() {
        for (Png p : getLayer().getPngs()) {
            if (p.isNephewOf(this)) {
                return true;
            }
        }
        return false;
    }

    /**
     * for the constraint:  png <=> f1,f1  this method returns f1,f2
     *
     * @return
     */
    public BoolExpr getImplicant() {
        Set<Var> myVars = getFeatures();
        System.out.println("myVars.size() = " + myVars.size());

        Collection<Png> nephews = getNephews();
        if (nephews.size() == 0) {
            if (myVars.size() == 1) {
                return myVars.iterator().next();
            } else {
                LinkedHashSet<BoolExpr> a = new LinkedHashSet<BoolExpr>(myVars);
                And and = new And(a);
                return and;
            }
        } else {
            LinkedHashSet<BoolExpr> a = new LinkedHashSet<BoolExpr>(myVars);
            for (Png nephew : nephews) {
                a.add(nephew.getNephewExpression(this));
            }
            return new And(a);
        }
    }

    /**
     * Pre-condition: this is a nephew of uncle arg
     *
     * @param uncle
     * @return
     */
    private BoolExpr getNephewExpression(Png uncle) {
        String errorMessage = this + " is not a nephew of " + uncle;
        if (this.equals(uncle)) throw new IllegalStateException(errorMessage);

        Set<Var> uncleVars = uncle.getFeatures();
        Set<Var> nephewVars = this.getFeatures();

        if (nephewVars.size() <= uncleVars.size()) throw new IllegalStateException(errorMessage);
        if (!nephewVars.containsAll(uncleVars)) throw new IllegalStateException(errorMessage);

        LinkedHashSet<BoolExpr> a = new LinkedHashSet<BoolExpr>();
        for (Var v : nephewVars) {
            if (uncleVars.contains(v)) continue;
            a.add(new Not(v));
        }

        if (a.size() == 1) return a.iterator().next();
        else return new And(a);
    }

    private boolean isNephewOf(Png uncle) {

        if (this.equals(uncle)) return false;

        Set<Var> uncleVars = uncle.getFeatures();
        Set<Var> nephewVars = this.getFeatures();

        return nephewVars.size() > uncleVars.size() && nephewVars.containsAll(uncleVars);
    }

    public List<String> getPath() {
        ArrayList<String> a = new ArrayList<String>();
        Node p = this;
        while (true) {
            a.add(0, p.getSimpleName());
            p = p.getParent();
            if (p.isImageModel()) break;
        }
        return a;
    }

//    @Override
//    public String toString() {
//        return getPath().toString().replace(",", "/").replace("[", "").replace("[", "").replace(" ", "");
//    }

    @Override
    public String toString() {
        return "P[" + shortSha + "]";
    }

    public void pick() {
        assert layer != null;
        layer.currentPng = this;
    }

    public boolean isPicked() {
        return layer.currentPng == this;
    }

    public Path getUrl(Path imageBase) {
        return imageBase.append("pngs").append(shortSha + ".png");
    }


    public boolean isVisible() {
        return visible;
    }

    @Override
    public boolean isBlink() {
        throw new UnsupportedOperationException();
    }
}
