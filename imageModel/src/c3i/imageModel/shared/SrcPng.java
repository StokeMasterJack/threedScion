package c3i.imageModel.shared;

import c3i.featureModel.shared.common.SimplePicks;
import smartsoft.util.shared.Path;

import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


@Immutable
public class SrcPng<V> extends ImChildBase<V> implements ImFeatureOrPng<V>, IsLeaf<V> {

    private final int angle;
    private final PngShortSha shortSha;

    public static final String VERSION_PREFIX = "vr_1_";
    public static final String PNG_SUFFIX = ".png";


    @Override
    public String toString() {
        return super.toString() + "  " + shortSha + "  Z:" + (isZLayer() ? "T" : "F");
    }

    public SrcPng(int depth, int angle, PngShortSha shortSha) {
        super(depth);
        this.angle = angle;
        this.shortSha = shortSha;
    }


    public int getAngle() {
        return angle;
    }

    @Override
    public String getName() {
        return getVersionPrefix() + getAnglePadded() + getPngSuffix();
    }

    public String getVarCode() {
        return toString().replace("/", "").replace("", "").replace("_", "X");
    }

    public static String getName(int angle) {
        return VERSION_PREFIX + Angle.getAnglePadded(angle) + PNG_SUFFIX;
    }

    private String getAnglePadded() {
        return Angle.getAnglePadded(angle);
    }

    private String getVersionPrefix() {
        return VERSION_PREFIX;
    }

    public String getPngSuffix() {
        return PNG_SUFFIX;
    }

    @Override
    public boolean isFeature() {
        return false;
    }

    @Override
    public boolean isPng() {
        return true;
    }


    @Override
    public void getMatchingPngs(PngMatch bestMatch, SimplePicks picks, int angle) {
        if (this.angle == angle) {
            bestMatch.add(this);
        }
    }

    private String getDeltaYSuffix(SimplePicks picks, int angle) {
        int liftAmount = getDeltaY(picks, angle);
        if (liftAmount == 0) {
            return "";
        } else if (liftAmount > 0 && liftAmount < 10) {
            return "0" + liftAmount;
        } else if (liftAmount >= 10 && liftAmount < 100) {
            return "" + liftAmount;
        } else {
            throw new IllegalStateException("Invalid lift amount: " + liftAmount);
        }
    }

    public int getDeltaY(SimplePicks picks, int angle) {
        ImLayer layer = getLayer();
        return layer.getDeltaY(picks, angle);
    }

    public boolean hasFeature(String varCode) {
        IsParent p = parent;
        while (p.isFeature()) {
            ImFeature imFeature = (ImFeature) p;
            Object var = imFeature.getVar();
            String code = var.toString();
            if (code.equals(varCode)) return true;
            p = imFeature.getParent();
        }

        return false;
    }

    public Set<V> getFeatures() {
        HashSet<V> vars = new HashSet<V>();
        getFeatures(vars);
        return vars;
    }

    public void getFeatures(Set<V> features) {
        IsParent p = parent;
        while (p.isFeature()) {
            ImFeature<V> f = p.asFeature();
            V var = f.getVar();
            features.add(var);
            p = p.getParent();
        }
    }

    public int getFeatureCount() {
        int fc = 0;
        IsParent p = parent;
        while (p.isFeature()) {
            fc++;
            p = p.getParent();
        }
        return fc;
    }

    public static boolean hasPngSuffix(String localName) {
        return localName.endsWith(PNG_SUFFIX);
    }

    public static boolean canParseAngeFromLocalName(String localName) {
        try {
            getAngleFromPng(localName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static int getAngleFromPng(String localName) {
        String s1 = localName.replace(SrcPng.VERSION_PREFIX, "");
        String s2 = s1.replaceAll(SrcPng.PNG_SUFFIX, "");
        return Integer.parseInt(s2);
    }

    public static boolean isValidLocalName(String localName) {
        return hasPngSuffix(localName) && canParseAngeFromLocalName(localName);
    }

    @Override
    public boolean containsAngle(int angle) {
        return this.angle == angle;
    }

    public SrcPng<V> copy(int angle) {
        if (this.angle == angle) return new SrcPng(depth, angle, shortSha);
        else throw new IllegalStateException();
    }

    @Override
    public void getVarSet(Set<V> varSet) {
        //intentionally blank
    }

    @Override
    public void getVarSet(Set<V> varSet, int angle) {
        //intentionally blank
        if (this.angle == angle) {
            Set<V> features = getFeatures();
            varSet.addAll(features);
        }
    }

    @Override
    public void getPngs(Set<SrcPng<V>> pngs) {
        pngs.add(this);
    }

    public ImageModel getSeries() {
        return getView().getSeries();
    }

    public ImView getView() {
        ImNode n = this;
        while (true) {
            n = n.getParent();
            if (n.isView()) return n.asView();
        }
    }

    public ImLayer getLayer() {
        ImNode n = this;
        while (true) {
            n = n.getParent();
            if (n.isLayer()) return n.asLayer();
        }
    }

//    public Path getViewRelativePath() {
//        Path pngPath = getPath();
//        Path layerPath = getView().getPath();
//        return pngPath.leftTrim(layerPath);
//    }
//
//    public Path getLayerRelativePath() {
//        Path pngPath = getPath();
//        Path layerPath = getLayer().getPath();
//        return pngPath.leftTrim(layerPath);
//    }

    public boolean isPartOfJpg() {
        return !isZLayer();
    }

    public boolean isAccessory() {
        return getLayer().isAccessory();
    }

    public String getShortSha() {
        return shortSha.stringValue();
    }


    public boolean isZLayer() {
        return getLayer().isZLayer();
    }

    public boolean isBackground() {
        return getLayer().isBackground();
    }

    public Collection<SrcPng> getYoungerCousins() {


        return null;
    }


    public Path getThreedBaseUrl(Path repoBaseUrl) {
        return getView().getSeries().getThreedBaseUrl(repoBaseUrl);
    }


    @Override
    public Path getUrl(Path repoBaseUrl) {
        ImView view = getView();
        ImageModel series = view.getSeries();
        return series.getThreedBaseUrl(repoBaseUrl).append("pngs").append("source").append(getShortSha()).appendName(".png");
    }

    public Path getUrl() {
        return getUrl(null);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != SrcPng.class) return false;
        SrcPng that = (SrcPng) obj;
        if (angle != that.angle) return false;
        return shortSha.equals(that.shortSha);
    }

    @Override
    public int hashCode() {
        return shortSha.hashCode();
    }

    public Object getLiftTrigger() {
        return getLayer().getView().getLiftSpec().getTriggerFeature();
    }


}
