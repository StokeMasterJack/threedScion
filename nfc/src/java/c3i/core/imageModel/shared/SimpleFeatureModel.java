package c3i.core.imageModel.shared;

import c3i.core.common.shared.SeriesKey;

public interface SimpleFeatureModel {

    Object get(String varCode);

    SeriesKey getSeriesKey();
}
