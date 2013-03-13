package c3i.imageModel.shared;

public interface IBaseImageKey {
    Profile getProfile();

    String getFingerprint();

    ImContextKey getSeriesKey();

    RawBaseImage getPngKeys();

    boolean isJpg();
}
