package c3i.featureModel.shared.common;

/**
 * Should implement fast hash and equals
 */
public interface SimplePicks<V> {

    boolean isPicked(V var);

    boolean isValidBuild();
}
