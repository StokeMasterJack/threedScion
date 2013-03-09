package c3i.imgGen.external;

/**
 * An out-complete product.
 *
 * It is assumed that this Product goes along with a particular context (csp) and outVarSet
 * @param <V>
 */
public interface Product1<V> {

    boolean isTrue(V var);

}
