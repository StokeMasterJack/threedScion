package c3i.imageModel.shared;

import c3i.featureModel.shared.common.SeriesKey;

public interface ImContext<V> {

    /**
     *  returns Var from feature model or null if varCode not found
     */
    V resolveVar(String varCode);

    SeriesKey getContextKey();

}
