package c3i.core.imageModel.shared;

import c3i.core.featureModel.shared.boolExpr.Var;
import smartsoft.util.lang.shared.Path;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents one segment of a png layer stack like this:
 *
 * 7126703-b357925-63985d3
 *
 */
public class PngSpec {

    private final SrcPng srcPng;
    private final int deltaY;

    public PngSpec(SrcPng srcPng, int deltaY) {
        this.srcPng = srcPng;
        this.deltaY = deltaY;
    }

    public String serializeToUrlSegment() {
        return PngSegmentKey.serializeUrlSegment(getShortSha(), deltaY);
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

    public PngSegmentKey getKey(){
        return new PngSegmentKey(srcPng.getShortSha(),deltaY);
    }
    public Path getUrl(Path repoBaseUrl) {
        ImView view = srcPng.getView();
        ImSeries series = view.getSeries();

        String serial = getSegmentString();
        return series.getThreedBaseUrl(repoBaseUrl).append("pngs").append(serial).appendName(".png");
    }

    private String getSegmentString() {
        return getKey().serializeToUrlSegment();
    }

    public int getDeltaY() {
        return deltaY;
    }

    public SrcPng getSrcPng() {
        return srcPng;
    }

}
