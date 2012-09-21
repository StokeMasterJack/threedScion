package c3i.core.imageModel.shared;

import c3i.core.common.shared.SeriesKey;
import c3i.core.threedModel.shared.Slice2;
import com.google.common.collect.ImmutableList;
import smartsoft.util.lang.shared.Path;

import javax.annotation.concurrent.Immutable;

@Immutable
public class BaseImage extends AbstractImImage implements IBaseImageKey {

    public static final String FINGERPRINT_SEPARATOR = "-";

    private final Slice2 slice;

    private final String fingerprint;

    private final ImmutableList<PngKey> pngKeys;
    private final int hash;

    public BaseImage(Profile profile, Slice2 slice, ImmutableList<PngKey> pngKeys) {
        super(profile);
        assert slice != null;
        this.slice = slice;
        this.pngKeys = pngKeys;
        this.fingerprint = generateFingerprint(profile, pngKeys);
        this.hash = fingerprint.hashCode();
    }

    public ImmutableList<PngKey> getPngKeys() {
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


    public static BaseImage parse(Profile profile, Slice2 slice, String fp) {
        ImmutableList.Builder<PngKey> builder = ImmutableList.builder();
        SeriesKey sk = slice.getView().getSeries().getSeriesKey();
        String[] pngSegments = fp.split("-");
        for (String pngSegment : pngSegments) {
            builder.add(new PngKey(pngSegment));
        }
        ImmutableList<PngKey> zPngKeys = builder.build();
        return new BaseImage(profile, slice, zPngKeys);
    }

    public String getFingerprint() {
        return generateFingerprint(profile, pngKeys);
    }

    public static String generateFingerprint(Profile profile, ImmutableList<? extends PngKey> allPngs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < allPngs.size(); i++) {
            PngKey srcPng = allPngs.get(i);
            String pngUrlSegment = srcPng.serializeToUrlSegment();
            sb.append(pngUrlSegment);
            boolean last = (i == allPngs.size() - 1);
            if (!last) sb.append(FINGERPRINT_SEPARATOR);
        }
        return sb.toString();
    }

    @Override
    public Path getUrl(Path repoBaseUrl) {
        ImSeries series = slice.getView().getSeries();
        String ext = profile.getBaseImageType().getFileExtension();
        return series.getThreedBaseUrl(repoBaseUrl).append("jpgs").append(profile.getKey()).append(fingerprint).appendName("." + ext);
    }

    public ImView getView() {
        return slice.getView();
    }

    public SeriesKey getSeriesKey() {
        return slice.getView().getSeries().getSeriesKey();
    }

    @Override
    public boolean isLayerPng() {
        return false;
    }
}