package c3i.core.imageModel.shared;

import com.google.common.collect.ImmutableList;

import java.io.Serializable;

public class PngSegments implements Serializable {

    public static final String FINGERPRINT_SEPARATOR = "-";
    private static final long serialVersionUID = -2941099965195410437L;

    private String fingerprint;

    public PngSegments(ImmutableList<PngSegment> pngs) throws IllegalArgumentException {
        fingerprint = generateFingerprint(pngs);
    }

    public PngSegments(String fingerprint) throws IllegalArgumentException {

        if (fingerprint == null) {
            throw new IllegalArgumentException("Invalid PngSegments fingerprint: pngSegments fingerprint cannot be null");
        }

        fingerprint = fingerprint.trim();

        if (fingerprint.length() == 0) {
            throw new IllegalArgumentException("Invalid PngSegments fingerprint: pngSegments fingerprint cannot be empty");
        }


        this.fingerprint = fingerprint;
    }

    private PngSegments() {
    }

    public ImmutableList<PngSegment> getPngs() {
        return parse(fingerprint);
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public static ImmutableList<PngSegment> parse(String fingerprint) throws IllegalArgumentException {
        if (fingerprint == null) {
            throw new IllegalArgumentException("Invalid PngSegments fingerprint: pngSegments fingerprint cannot be null");
        }

        fingerprint = fingerprint.trim();

        if (fingerprint.length() == 0) {
            throw new IllegalArgumentException("Invalid PngSegments fingerprint: pngSegments fingerprint cannot be empty");
        }

        ImmutableList.Builder<PngSegment> builder = ImmutableList.builder();
        String[] pngSegments = fingerprint.split("-");
        for (String pngSegment : pngSegments) {
            PngSegment ps = null;
            try {
                ps = new PngSegment(pngSegment);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid PngSegments fingerprint[" + fingerprint + "]", e);
            }
            builder.add(ps);
        }

        return builder.build();
    }

    public static String generateFingerprint(ImmutableList<PngSegment> pngs) throws IllegalArgumentException {
        if (pngs == null) {
            throw new IllegalArgumentException("Invalid PngSegments fingerprint: pngs cannot be null");
        }
        if (pngs.isEmpty()) {
            throw new IllegalArgumentException("Invalid PngSegments fingerprint: pngs cannot be empty");
        }

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
        PngSegments that = (PngSegments) o;
        return fingerprint.equals(that.fingerprint);
    }

    @Override
    public int hashCode() {
        return fingerprint.hashCode();
    }
}
