package c3i.core.imageModel.shared;

import com.google.common.collect.ImmutableList;

public class PngSegments {

    public static final String FINGERPRINT_SEPARATOR = "-";

    private final ImmutableList<PngSegment> pngs;

    public PngSegments(ImmutableList<PngSegment> pngs) {
        this.pngs = pngs;
    }

    public ImmutableList<PngSegment> getPngs() {
        return pngs;
    }

    public String getFingerprint() {
        return generateFingerprint(pngs);
    }


    public static String generateFingerprint(ImmutableList<PngSegmentKey> allPngs) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < allPngs.size(); i++) {
                PngSegmentKey srcPng = allPngs.get(i);
                String pngUrlSegment = srcPng.serializeToUrlSegment();
                sb.append(pngUrlSegment);
                boolean last = (i == allPngs.size() - 1);
                if (!last) sb.append(FINGERPRINT_SEPARATOR);
            }
            return sb.toString();
        }
}
