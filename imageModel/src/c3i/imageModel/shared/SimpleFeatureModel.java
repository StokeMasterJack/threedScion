package c3i.imageModel.shared;

public interface SimpleFeatureModel {

    Object getVar(String varCode);

    SeriesKey getSeriesKey();

    boolean containsVarCode(String varCode);
}
