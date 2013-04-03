package c3i.imageModel.shared;

//import c3i.featureModel.shared.common.SeriesKey;
import c3i.featureModel.shared.common.SeriesKey;
import smartsoft.util.shared.Path;

import javax.annotation.concurrent.Immutable;

@Immutable
public class BaseImage extends AbstractImImage implements IBaseImageKey {


    private final Slice2 slice;

    private final String fingerprint;

    private final RawBaseImage pngKeys;
    private final int hash;

    public BaseImage(Profile profile, Slice2 slice, RawBaseImage pngKeys) {
        super(profile);
        assert slice != null;
        this.slice = slice;
        this.pngKeys = pngKeys;
        this.fingerprint = pngKeys.getFingerprint();
        this.hash = fingerprint.hashCode();
    }

    public RawBaseImage getPngKeys() {
        return pngKeys;
    }

    @Override
    public boolean isJpg() {
        return profile.isJpg();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseImage that = (BaseImage) o;
        return fingerprint.equals(that.fingerprint) && profile.equals(that.profile);

    }

    @Override
    public int hashCode() {
        return hash;
    }

    public static BaseImage parse(Profile profile, Slice2 slice, RawBaseImage fingerprint) {
        return new BaseImage(profile, slice, fingerprint);
    }

    public static BaseImage parse(Profile profile, Slice2 slice, String fp) {
        return new BaseImage(profile, slice, new RawBaseImage(fp));
    }

    public String getFingerprint() {
        return pngKeys.getFingerprint();
    }


    @Override
    public Path getUrl(Path repoBaseUrl) {
        ImageModel series = slice.getView().getSeries();
        String ext = profile.getBaseImageType().getFileExtension();
        return series.getThreedBaseUrl(repoBaseUrl).append("jpgs").append(profile.getKey()).append(fingerprint).appendName("." + ext);
    }

    public ImView getView() {
        return slice.getView();
    }

    public SeriesKey getSeriesKey() {
        return slice.getView().getSeries().getKey();
    }

    @Override
    public boolean isLayerPng() {
        return false;
    }
}