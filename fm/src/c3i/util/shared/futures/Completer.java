package c3i.util.shared.futures;

/**
 * A [Completer] is used to produce [Future]s and supply their value when it
 * becomes available.
 *
 * A service that provides values to callers, and wants to return [Future]s can
 * use a [Completer] as follows:
 *
 *   Completer completer = new Completer();
 *   // send future object back to client...
 *   return completer.future;
 *   ...
 *
 *   // later when value is available, call:
 *   completer.complete(value);
 *
 *   // alternatively, if the service cannot produce the value, it
 *   // can provide an exception:
 *   completer.completeException(exception);
 *
 */
public interface Completer<T> {

    /** The future that will contain the value produced by this completer. */
    Future<T> getFuture();

    /** Supply a value for [future]. */
    void setResult(T value);

    /**
     * Indicate in [future] that an exception occured while trying to produce its
     * value. The argument [exception] should not be [:null:].
     */
    void setException(Throwable exception);

}
