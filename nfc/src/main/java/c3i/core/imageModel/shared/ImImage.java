package c3i.core.imageModel.shared;

import c3i.core.common.shared.SeriesKey;
import smartsoft.util.shared.Path;

public interface ImImage {

    Path getUrl(Path repoBase);
    Profile getProfile();
    boolean isLayerPng();

    SeriesKey getSeriesKey();
    boolean isScionImage();
}
