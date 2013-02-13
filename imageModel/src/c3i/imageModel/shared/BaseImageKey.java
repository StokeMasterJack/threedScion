package c3i.imageModel.shared;

import com.google.common.base.Preconditions;

public class BaseImageKey implements IBaseImageKey{

    private final SeriesKey seriesKey;
    private final Profile profile;
    private final String fingerprint;  //example: 0e24056-80e3097 (i.e. png1-png2-png3 etc)

    private final RawBaseImage zPngKeys;

    /**
     *
     * @param seriesKey
     * @param profile
     * @param fingerprint  //example: 0e24056-80e3097 (i.e. png1-png2-png3 etc)
     */
    public BaseImageKey(SeriesKey seriesKey, Profile profile, String fingerprint) {
        Preconditions.checkNotNull(profile);
        profile.getBaseImageType();
        this.seriesKey = seriesKey;
        this.profile = profile;
        this.fingerprint = fingerprint;
        this.zPngKeys = initZPngKeys(fingerprint);
    }

    private static RawBaseImage initZPngKeys(String fp) {
        return new RawBaseImage(fp);
    }


    public SeriesKey getSeriesKey() {
        return seriesKey;
    }

    public Profile getProfile() {
        return profile;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseImageKey jpgKey = (BaseImageKey) o;

        if (!fingerprint.equals(jpgKey.fingerprint)) return false;
        if (!seriesKey.equals(jpgKey.seriesKey)) return false;
        if (!profile.equals(jpgKey.profile)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = seriesKey.hashCode();
        result = 31 * result + profile.hashCode();
        result = 31 * result + fingerprint.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return fingerprint;
    }

    public boolean hasAnyLift() {
        String[] a = fingerprint.split("-");
        for (String pngSegment : a) {
            if (pngSegment.length() == 9) {
                return true;
            }
        }
        return false;
    }



    @Override
    public RawBaseImage getPngKeys() {
        return zPngKeys;
    }

    @Override
    public boolean isJpg() {
        return profile.isJpg();
    }
}
