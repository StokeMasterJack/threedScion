package c3i.imageModel.shared;

import com.google.common.collect.ImmutableList;
import smartsoft.util.shared.Path;

import java.io.Serializable;

public class RawBaseImage implements Serializable {

    public static final String FINGERPRINT_SEPARATOR = "-";
    private static final long serialVersionUID = -2941099965195410437L;

    private String fingerprint;

    public RawBaseImage(ImmutableList<PngSegment> pngs) {
        fingerprint = generateFingerprint(pngs);
    }

    public RawBaseImage(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    private RawBaseImage() {
    }

    public ImmutableList<PngSegment> getPngs() {
        return parse(fingerprint);
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public static ImmutableList<PngSegment> parse(String fingerprint) {
        ImmutableList.Builder<PngSegment> builder = ImmutableList.builder();
        String[] pngSegments = fingerprint.split("-");
        for (String pngSegment : pngSegments) {
            builder.add(new PngSegment(pngSegment));
        }
        return builder.build();
    }

    public static String generateFingerprint(ImmutableList<PngSegment> pngs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pngs.size(); i++) {
            PngSegment srcPng = pngs.get(i);
            String pngUrlSegment = srcPng.serializeToUrlSegment();
            sb.append(pngUrlSegment);
            boolean last = (i == pngs.size() - 1);
            if (!last) sb.append(FINGERPRINT_SEPARATOR);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return getFingerprint();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RawBaseImage that = (RawBaseImage) o;
        return fingerprint.equals(that.fingerprint);
    }

    @Override
    public int hashCode() {
        return fingerprint.hashCode();
    }

}
