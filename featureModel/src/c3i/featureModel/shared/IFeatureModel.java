package c3i.featureModel.shared;

public interface IFeatureModel<K, V> {

    K getKey();

    V getVar(int varIndex) throws UnknownVarIndexException;

    V getVar(String varCode) throws UnknownVarCodeException;



}
