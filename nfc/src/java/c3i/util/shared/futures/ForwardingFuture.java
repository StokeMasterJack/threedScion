package c3i.util.shared.futures;

import c3i.util.shared.events.LoadState;

public class ForwardingFuture<T> implements Future<T> {

    protected final Future<T> delegate;

    public ForwardingFuture(Future<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public T getResult() throws RuntimeException {
        return delegate.getResult();
    }

    @Override
    public Throwable getException() {
        return delegate.getException();
    }

    @Override
    public boolean isComplete() {
        return delegate.isComplete();
    }

    @Override
    public boolean isLoaded() {
        return delegate.isLoaded();
    }

    public void success(OnSuccess<T> successHandler) {
        delegate.success(successHandler);
    }

    @Override
    public void failure(OnException exceptionHandler) {
        delegate.failure(exceptionHandler);
    }

    public <O> Future transform(SyncTransform<T, O> transformation) {
        return delegate.transform(transformation);
    }

    public <O> Future chain(AsyncTransform<T, O> transformation) {
        return delegate.chain(transformation);
    }

    @Override
    public boolean isFailed() {
        return delegate.isFailed();
    }

    @Override
    public void complete(OnComplete onComplete) {
        delegate.complete(onComplete);
    }

    @Override
    public boolean isLoading() {
        return delegate.isLoading();
    }

    @Override
    public LoadState getState() {
        return delegate.getState();
    }
}
