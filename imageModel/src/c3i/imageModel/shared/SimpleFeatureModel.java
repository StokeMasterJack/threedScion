package c3i.imageModel.shared;

public interface SimpleFeatureModel<V> {

    V getVar(String varCode);

    ImageModelKey getSeriesKey();

    boolean containsVarCode(String varCode);
}
