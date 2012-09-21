package c3i.core.imageModel.shared;

import c3i.core.common.shared.SeriesKey;
import com.google.common.collect.ImmutableList;

public interface IBaseImageKey {
    Profile getProfile();

    String getFingerprint();

    SeriesKey getSeriesKey();

    ImmutableList<PngKey> getPngKeys();

    boolean isJpg();
}
