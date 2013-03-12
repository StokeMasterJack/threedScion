package c3i.imageModel.shared;

public interface IBaseImageKey {
    Profile getProfile();

    String getFingerprint();

    ImageModelKey getSeriesKey();

    RawBaseImage getPngKeys();

    boolean isJpg();
}
