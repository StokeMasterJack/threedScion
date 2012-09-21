package c3i.util.shared.futures;


import c3i.util.shared.events.LoadState;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;

/**
 * A [Future] is used to obtain a value sometime in the future.  Receivers of a
 * [Future] obtain the value by passing a callback to [then]. For example:
 *
 *   Future<int> future = getFutureFromSomewhere();
 *   future.then((value) {
 *     print("I received the number $value");
 *   });
 */

public interface Future<T> extends Exportable{

    /** The value provided. Throws an exception if [hasValue] is false. */
    @Export
    T getResult() throws RuntimeException;

    /**
     * Exception that occurred ([:null:] if no exception occured). This property
     * throws a [FutureNotCompleteException] if it is used before this future is
     * completes.
     */
    Throwable getException();

    /**
     * Whether the future is complete (either the value is available or there was
     * an exception).
     */
    boolean isComplete();

    /**
     * Whether the value is available (meaning [isComplete] is true, and there was
     * no exception).
     */
    boolean isLoaded();

    /**
     * When this future is complete and has a value, then [onComplete] is called
     * with the value.
     */
    void success(OnSuccess<T> successHandler);

    /**
     * If this future gets an exception, then call [onException].
     *
     * If [onException] returns true, then the exception is considered handled.
     *
     * If [onException] does not return true (or [handleException] was never
     * called), then the exception is not considered handled. In that case, if
     * there were any calls to [then], then the exception will be thrown when the
     * value is set.
     *
     * In most cases it should not be necessary to call [handleException],
     * because the exception associated with this [Future] will propagate
     * naturally if the future's value is being consumed. Only call
     * [handleException] if you need to do some special local exception handling
     * related to this particular Future's value.
     */
    void failure(OnException exceptionHandler);

    /**
     * A future representing [transformation] applied to this future's value.
     *
     * When this future gets a value, [transformation] will be called on the
     * value, and the returned future will receive the result.
     *
     * If an exception occurs (received by this future, or thrown by
     * [transformation]) then the returned future will receive the exception.
     *
     * You must not add exception handlers to [this] future prior to calling
     * transform, and any you add afterwards will not be invoked.
     */
    <O> Future transform(SyncTransform<T, O> transformation);

    /**
     * A future representing an asynchronous transformation applied to this
     * future's value. [transformation] must return a Future.
     *
     * When this future gets a value, [transformation] will be called on the
     * value. When the resulting future gets a value, the returned future
     * will receive it.
     *
     * If an exception occurs (received by this future, thrown by
     * [transformation], or received by the future returned by [transformation])
     * then the returned future will receive the exception.
     *
     * You must not add exception handlers to [this] future prior to calling
     * chain, and any you add afterwards will not be invoked.
     */
    <O> Future chain(AsyncTransform<T, O> transformation);

    boolean isFailed();

    void complete(OnComplete onComplete);

    boolean isLoading();

    LoadState getState();
}


