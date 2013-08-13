package c3i.util.shared.futures;


import c3i.util.shared.events.LoadState;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FutureImpl<T> implements Future<T> {

    public static ExceptionHandlerFactory exceptionHandlerFactory = new DefaultExceptionHandlerFactory();

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
    public boolean isSuccess() {
        return _isComplete && _exception == null;
    }

    @Override
    public void success(OnSuccess<T> successHandler) throws RuntimeException {
        if (isSuccess()) {
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
        if (isSuccess()) {
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
    public boolean isProcessing() {
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

    private static Logger log = Logger.getLogger(FutureImpl.class.getName());

    void _complete() {

        _isComplete = true;
        if (_exception != null) {

            if (exceptionHandlers.size() == 0) {
                log.log(Level.WARNING, "An exception occurred while executing an async method but no exception handlers was registered.");
            }

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

        if (isSuccess()) {
            for (OnSuccess<T> successHandler : successHandlers) {
                T result = getResult();
                try {
                    successHandler.onSuccess(result);
                } catch (RuntimeException e) {
                    log.log(Level.SEVERE, "An uncaught exception was thrown from callback onSuccess(" + result + ")", e);
                }
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
        final CompleterImpl<O> completer = new CompleterImpl<O>();


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
        final CompleterImpl<Future<O>> completer = new CompleterImpl<Future<O>>();

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
        } else if (isSuccess()) {
            return LoadState.LOADED;
        } else if (isProcessing()) {
            return LoadState.LOADING;
        } else {
            throw new IllegalStateException();
        }

    }


    private static class DefaultExceptionHandlerFactory implements ExceptionHandlerFactory {

        @Override
        public OnException createExceptionHandler(final Future future) {

            return new OnException() {
                @Override
                public boolean onException(Throwable e) {
                    String msg = "Exception occurred while processing future action[" + future + "]";
                    log.log(Level.SEVERE, msg, e);
                    //String render = ExceptionRenderer.render(e);
                    return true;
                }
            };
        }


    }
}
