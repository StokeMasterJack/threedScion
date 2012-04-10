package threed.core.imageModel.shared;

/**
 * First 7 chars of a 40 digit sha
 */
public class PngShortSha {

    public static int SHORT_SHA_LENGTH = 7;
    public static int FULL_SHA_LENGTH = 40;

    private final String value;

    public PngShortSha(String pngSha) {
        assert pngSha != null;
        if (pngSha.length() == SHORT_SHA_LENGTH) {
            this.value = pngSha;
        } else if (pngSha.length() > SHORT_SHA_LENGTH && pngSha.length() >= FULL_SHA_LENGTH) {
            this.value = pngSha.substring(0, 7);
        } else {
            throw new IllegalArgumentException("Bad pngSha argument [" + pngSha + "]. pngSha.length must be >= " + SHORT_SHA_LENGTH + " and <= " + FULL_SHA_LENGTH);
        }
    }

    @Override
    public String toString() {
        return stringValue();
    }

    public String stringValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PngShortSha that = (PngShortSha) o;

        if (!value.equals(that.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
