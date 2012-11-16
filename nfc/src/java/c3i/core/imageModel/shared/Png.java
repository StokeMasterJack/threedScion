package c3i.core.imageModel.shared;

import smartsoft.util.shared.Path;

import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class Png extends AbstractImImage {

    private final PngSpec basePng;

    public Png(Profile profile, PngSpec srcPng) {
        super(profile);
        this.basePng = srcPng;
    }

    public Path getUrl(Path repoBaseUrl) {
        String profileKey = profile.getKey();
        ImSeries series = basePng.getSeries();
        String pngUrlSegment = basePng.serializeToUrlSegment();
        Path threedBaseUrl = series.getThreedBaseUrl(repoBaseUrl);
        Path pngs = threedBaseUrl.append("pngs");
        return pngs.append(pngUrlSegment).appendName(".png");
//        return pngs.append(profileKey).append(shortSha).appendName(".png");  //todo
    }

    public PngSpec getSrcPng() {
        return basePng;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Png imPng = (Png) o;
        return basePng.equals(imPng.basePng) && profile.equals(imPng.profile);
    }

    @Override
    public int hashCode() {
        int result = profile.hashCode();
        result = 31 * result + basePng.hashCode();
        return result;
    }


}
