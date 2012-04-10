package threed.core.threedModel.shared;

public class JpgKey {

    private final SeriesKey seriesKey;
    private final JpgWidth width;
    private final String fingerprint;  //example: 0e24056-80e3097 (i.e. png1-png2-png3 etc)

    /**
     *
     * @param seriesKey
     * @param width
     * @param fingerprint  //example: 0e24056-80e3097 (i.e. png1-png2-png3 etc)
     */
    public JpgKey(SeriesKey seriesKey, JpgWidth width, String fingerprint) {
        this.seriesKey = seriesKey;
        this.width = width;
        this.fingerprint = fingerprint;
    }


    public SeriesKey getSeriesKey() {
        return seriesKey;
    }

    public JpgWidth getWidth() {
        return width;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JpgKey jpgKey = (JpgKey) o;

        if (!fingerprint.equals(jpgKey.fingerprint)) return false;
        if (!seriesKey.equals(jpgKey.seriesKey)) return false;
        if (!width.equals(jpgKey.width)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = seriesKey.hashCode();
        result = 31 * result + width.hashCode();
        result = 31 * result + fingerprint.hashCode();
        return result;
    }

    @Override public String toString() {
        return fingerprint;
    }
}
