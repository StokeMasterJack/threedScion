package c3i.core.imageModel.shared;

import c3i.core.common.shared.SeriesKey;
import c3i.core.featureModel.shared.boolExpr.Var;

public interface SimpleFeatureModel {

    Var get(String varCode);

    SeriesKey getSeriesKey();
}
