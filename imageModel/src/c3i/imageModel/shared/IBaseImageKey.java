package c3i.imageModel.shared;

import c3i.featureModel.shared.common.SeriesKey;

public interface IBaseImageKey {
    Profile getProfile();

    String getFingerprint();

    SeriesKey getSeriesKey();

    RawBaseImage getPngKeys();

    boolean isJpg();
}
