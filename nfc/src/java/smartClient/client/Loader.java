package smartClient.client;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * Loads one thing, but with potentially multiple listeners (future holders)
 *
 * @param <S> the result type
 */
public class Loader<S> implements Exportable {

    private final ArrayList<Future<S>> futures = new ArrayList<Future<S>>();

    private S result;
    private Throwable exception;

    private final String name;

    @Export
    public Loader(String name) {
        this.name = name;
    }

    public Loader() {
        this(null);
    }

    public void setResult(S result) {
        this.result = result;
        while (futures.size() > 0) {
            Future<S> f = futures.remove(0);
            f.setResult(result);
        }
    }

    public void setException(Throwable exception) {
        this.exception = exception;
        while (futures.size() > 0) {
            Future<S> f = futures.remove(0);
            f.setException(exception);
        }
    }


    @Nullable
    public S getResult() {
        return result;
    }

    @Nullable
    public Throwable getException() {
        return exception;
    }

    @Nonnull
    public Future<S> ensureLoaded() {
        Future.State state = getState();
        switch (state) {
            case LOADING:
                return newFutureStillLoading();
            case LOADED:
                return newFutureAlreadyCompleteSuccess();
            case FAILED:
                return newFutureAlreadyCompleteFailed();
            default:
                throw new IllegalStateException();
        }
    }


    private Future<S> newFutureStillLoading() {
        Future<S> future = createFuture(name);
        futures.add(future);
        return future;
    }

    private Future<S> newFutureAlreadyCompleteSuccess() {
        Future<S> f = createFuture(name);
        f.setResult(result);
        return f;
    }

    private Future<S> newFutureAlreadyCompleteFailed() {
        Future<S> f = createFuture(name);
        f.setException(exception);
        return f;
    }

    protected Future<S> createFuture(String name) {
        return new Future<S>(name);
    }

    public Future.State getState() {
        if (result == null && exception == null) {
            return Future.State.LOADING;
        } else if (result == null && exception != null) {
            return Future.State.FAILED;
        } else if (result != null && exception == null) {
            return Future.State.LOADED;
        } else {
            throw new IllegalStateException();
        }
    }

    public boolean isComplete() {
        return !isLoading();
    }

    public boolean isLoading() {
        return getState().equals(Future.State.LOADING);
    }

    public boolean isLoaded() {
        return getState().equals(Future.State.LOADED);
    }

    public boolean isFailed() {
        return getState().equals(Future.State.FAILED);
    }


}
