package c3i.util.shared.futures;


import c3i.util.shared.events.LoadState;

import javax.annotation.Nonnull;
import java.util.ArrayList;

class FutureImpl<T> implements Future<T> {

    private final ArrayList<OnComplete> completeHandlers = new ArrayList<OnComplete>();
    private final ArrayList<OnSuccess<T>> successHandlers = new ArrayList<OnSuccess<T>>();
    private final ArrayList<OnException> exceptionHandlers = new ArrayList<OnException>();

    private boolean _isComplete;
    private boolean _exceptionHandled;

    private T _value;

    private Throwable _exception;

    public Throwable getException() {
        if (!_isComplete) {
            throw new FutureNotCompleteException();
        }
        return _exception;
    }

    @Override
    public T getResult() throws RuntimeException {
        if (!_isComplete) {
            throw new FutureNotCompleteException();
        }
        if (_exception != null) {
            if (_exception instanceof RuntimeException) {
                throw ((RuntimeException) _exception);
            } else {
                throw new RuntimeException(_exception);
            }
        }
        return _value;
    }

    @Override
    public boolean isComplete() {
        return _isComplete;
    }

    @Override
    public boolean isLoaded() {
        return _isComplete && _exception == null;
    }

    @Override
    public void success(OnSuccess<T> successHandler) throws RuntimeException {
        if (isLoaded()) {
            successHandler.onSuccess(getResult());
        } else if (!isComplete()) {
            successHandlers.add(successHandler);
        } else if (!_exceptionHandled) {
            if (_exception instanceof RuntimeException) {
                throw ((RuntimeException) _exception);
            } else {
                throw new RuntimeException(_exception);
            }
        }
    }

    @Override
    public void complete(OnComplete completeHandler) {
        if (isLoaded()) {
            completeHandler.call();
        } else if (!isComplete()) {
            completeHandlers.add(completeHandler);
        } else if (!_exceptionHandled) {
            if (_exception instanceof RuntimeException) {
                throw ((RuntimeException) _exception);
            } else {
                throw new RuntimeException(_exception);
            }
        }
    }

    @Override
    public boolean isLoading() {
        return !isComplete();
    }

    @Override
    public void failure(OnException exceptionHandler) {
        if (_exceptionHandled) return;
        if (_isComplete) {
            if (_exception != null) {
                _exceptionHandled = exceptionHandler.onException(_exception);
            }
        } else {
            exceptionHandlers.add(exceptionHandler);
        }
    }

    void _complete() {
        _isComplete = true;
        if (_exception != null) {

            for (OnException exceptionHandler : exceptionHandlers) {
                //Explicitly check for true here so that if the handler returns null,
                //we don't get an exception in checked mode.
                boolean isHandled = exceptionHandler.onException(_exception);
                if (isHandled) {
                    _exceptionHandled = true;
                    break;
                }
            }
        }

        if (isLoaded()) {
            for (OnSuccess<T> successHandler : successHandlers) {
                successHandler.onSuccess(getResult());
            }
        } else {
            if (!_exceptionHandled && successHandlers.size() > 0) {
                if (_exception instanceof RuntimeException) {
                    throw ((RuntimeException) _exception);
                } else {
                    throw new RuntimeException(_exception);
                }

            }
        }

        for (OnComplete completeHandler : completeHandlers) {
            completeHandler.call();
        }
    }

    void _setValue(T value) {
        if (_isComplete) {
            throw new FutureAlreadyCompleteException();
        }
        _value = value;
        _complete();
    }

    void _setException(Throwable exception) {
        if (exception == null) {
            throw new IllegalArgumentException("null is not a legal value for the exception of a Future");
        }
        if (_isComplete) {
            throw new FutureAlreadyCompleteException();
        }
        _exception = exception;
        _complete();
    }

    @Override
    public <O> Future transform(final SyncTransform<T, O> transformation) {
        final Completer<O> completer = new CompleterImpl<O>();


        failure(new OnException() {
            @Override
            public boolean onException(Throwable e) {
                completer.setException(e);
                return true;
            }
        });


        success(new OnSuccess<T>() {
            @Override
            public void onSuccess(@Nonnull T v) {
                O transformed = null;
                try {
                    transformed = transformation.transform(v);
                } catch (RuntimeException e) {
                    completer.setException(e);
                    return;
                }
                completer.setResult(transformed);
            }
        });


        return completer.getFuture();
    }

    @Override
    public <O> Future chain(final AsyncTransform<T, O> transformation) {
        final Completer<Future<O>> completer = new CompleterImpl<Future<O>>();

        failure(new OnException() {
            @Override
            public boolean onException(Throwable e) {
                completer.setException(e);
                return true;
            }
        });


        success(new OnSuccess<T>() {
            @Override
            public void onSuccess(@Nonnull T v) {
                Future<O> transformed = null;
                try {
                    transformed = transformation.transform(v);
                } catch (RuntimeException e) {
                    completer.setException(e);
                    return;
                }
                completer.setResult(transformed);
            }
        });


        return completer.getFuture();
    }

    @Override
    public boolean isFailed() {
        return _isComplete && _exception != null;
    }

    public static <T> Future immediate(T value) {
        FutureImpl f = new FutureImpl();
        f._setValue(value);
        return f;
    }

    public LoadState getState() {
        if (isFailed()) {
            return LoadState.FAILED;
        } else if (isLoaded()) {
            return LoadState.LOADED;
        } else if (isLoading()) {
            return LoadState.LOADING;
        } else {
            throw new IllegalStateException();
        }

    }


}
