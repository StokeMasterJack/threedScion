package c3i.imageModel.shared;

public interface SimpleFeatureModel<V> {

    /**
     * @param varCode
     * @return null if varCode not found
     */
    V resolveVar(String varCode);

    ImageModelKey getSeriesKey();

}
