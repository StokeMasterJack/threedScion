package c3i.imageModel.shared;

public interface IBaseImageKey {
    Profile getProfile();

    String getFingerprint();

    SeriesKey getSeriesKey();

    RawBaseImage getPngKeys();

    boolean isJpg();
}
