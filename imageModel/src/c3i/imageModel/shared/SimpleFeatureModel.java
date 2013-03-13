package c3i.imageModel.shared;

public interface SimpleFeatureModel<V> {

    /**
     *  returns Var from feature model or null if varCode not found
     */
    V resolveVar(String varCode);

    ImContextKey getContextKey();

}
