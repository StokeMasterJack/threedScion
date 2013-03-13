package c3i.imageModel.shared;

/**
 * Should implement fast hash and equals
 */
public interface SimplePicks<V> {

    boolean isPicked(V var);

    boolean isValidBuild();
}
