package c3i.core.imageModel.shared;

import c3i.core.featureModel.shared.boolExpr.Var;
import smartsoft.util.lang.shared.Path;

import java.util.HashSet;
import java.util.Set;

public class PngSpec  {

    private final SrcPng srcPng;
    private final int deltaY;

    public PngSpec(SrcPng srcPng, int deltaY) {
        this.srcPng = srcPng;
        this.deltaY = deltaY;
    }

    public String serializeToUrlSegment() {
        return PngKey.serializeToUrlSegment(getShortSha(), deltaY);
    }

    public String getShortSha() {
        return srcPng.getShortSha();
    }

    public boolean isZLayer() {
        return srcPng.isZLayer();
    }

    public int indexOf(Var accessory) {
        return srcPng.indexOf(accessory);
    }

    public Set<Var> getFeatures() {
        if (!isLifted()) {
            return srcPng.getFeatures();
        } else {
            HashSet<Var> features = new HashSet<Var>(srcPng.getFeatures());
            Var f = srcPng.getLiftTrigger();
            features.add(f);
            return features;
        }
    }

    public boolean isLifted() {
        return deltaY != 0;
    }

    public ImSeries getSeries() {
        return srcPng.getSeries();
    }

    public ImLayer getLayer() {
        return srcPng.getLayer();
    }

    public boolean isBackground() {
        return srcPng.isBackground();
    }

//    public Path getUrl(Path repoBase) {
//        return srcPng.getUrl(repoBase);
//    }

    public PngKey getKey(){
        return new PngKey(srcPng.getShortSha(),deltaY);
    }
    public Path getUrl(Path repoBaseUrl) {
        ImView view = srcPng.getView();
        ImSeries series = view.getSeries();

        String serial = getKey().serializeToUrlSegment();
        return series.getThreedBaseUrl(repoBaseUrl).append("pngs").append(serial).appendName(".png");
    }

    public int getDeltaY() {
        return deltaY;
    }

    public SrcPng getSrcPng() {
        return srcPng;
    }

}
