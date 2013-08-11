package c3i.util.shared.futures;

/**
 *
 * A response only exist after completion.
 * It is not a future
 *
 * @param <V>
 */
public interface Response<K, V> {

    /**
     * @return the request (or key)
     */
    K getRequest();

    /**
     * @return the loaded or computed value
     */
    V get();

    Throwable getException();


}
