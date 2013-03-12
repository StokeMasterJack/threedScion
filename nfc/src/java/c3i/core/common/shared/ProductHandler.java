package c3i.core.common.shared;

public interface ProductHandler<P, R> {

    /**
     *
     * Fires once for each true, out-complete product.
     * That is: once per CSP where csp.isTrue() and csp.isOutComplete()
     *
     * @param product
     */
    void onProduct(P product);

    R getResult();


}
