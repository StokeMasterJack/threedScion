package c3i.imageModel.shared;

public interface SimpleFeatureModel {

    Object get(String varCode);

    SeriesKey getSeriesKey();

    boolean containsCode(String varCode);
}
