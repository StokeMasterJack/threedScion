package smartClient.client.util.futures;

import com.google.common.base.Preconditions;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.NoExport;
import smartClient.client.ThreedSession;
import smartClient.client.ViewSession;

@Export
public class Future<S> implements Exportable {

    public enum State {FAILED, LOADED, LOADING}

    private OnSuccess onSuccess;
    private OnFailure onFailure;
    private OnComplete onComplete;

    private boolean onSuccessCalled;
    private boolean onFailureCalled;
    private boolean onCompleteCalled;

    public S result;
    public Throwable exception;

    public static ExceptionHandler callbackExceptionHandler = new DefaultCallbackExceptionHandler();
    public static OnFailureFactory onFailureFactory = new DefaultOnFailureFactory();

    public final String name;

    @NoExport
    public Future() {
        this.name = null;
    }

    @NoExport
    public Future(String name) {
        this.name = name;
    }

    @NoExport
    public void setResult(S result) {
        Preconditions.checkNotNull(result);
        Preconditions.checkArgument(this.result == null);
        this.result = result;
        maybeCallOnSuccess();
        maybeCallOnComplete();
    }

    @NoExport
    public void setException(Throwable exception) {
        Preconditions.checkNotNull(exception);
        Preconditions.checkArgument(this.exception == null);
        this.exception = exception;
        maybeCallOnFailure();
        maybeCallOnComplete();
    }

    @NoExport
    private void maybeCallOnSuccess() {
        if (onSuccess != null && !onSuccessCalled && result != null) {
            onSuccessCalled = true;
            try {
                onSuccess.call();
            } catch (Exception e) {
                callbackExceptionHandler.handleException(name, e);
            }
        }
    }

    @NoExport
    private void maybeCallOnFailure() {
        if (onFailureCalled && exception != null) {
            try {
                if (onFailure != null) {
                    onFailure.call();
                } else {
                    onFailureFactory.createOnFailure(this, exception).call();
                }
                onFailureCalled = true;
            } catch (Exception e) {
                callbackExceptionHandler.handleException(name, e);
            }
        }
    }

    @NoExport
    private void maybeCallOnComplete() {
        if (onComplete != null && !onCompleteCalled && (result != null || exception != null)) {
            onCompleteCalled = true;
            try {
                onComplete.call();
            } catch (Exception e) {
                callbackExceptionHandler.handleException(name, e);
            }
        }
    }

    @Export
    public void success(OnSuccess onSuccess) {
        this.onSuccess = onSuccess;
        maybeCallOnSuccess();
    }

    @Export
    public void failure(OnFailure onFailure) {
        this.onFailure = onFailure;
        maybeCallOnFailure();
    }

    @Export
    public void complete(OnComplete onComplete) {
        this.onComplete = onComplete;
        maybeCallOnComplete();
    }

    @Export
    public S getResult() {
        return result;
    }

    @Export
    public Throwable getException() {
        return exception;
    }

    @Export
    public State getState() {
        if (result == null && exception == null) {
            return State.LOADING;
        } else if (result == null && exception != null) {
            return State.FAILED;
        } else if (result != null && exception == null) {
            return State.LOADED;
        } else {
            throw new IllegalStateException();
        }
    }

    @Export
    public boolean isSuccess() {
        return getState().equals(State.LOADED);
    }

    @Export
    public boolean isFailed() {
        return getState().equals(State.FAILED);
    }

    @Export
    public boolean isComplete() {
        return !isPending();
    }

    @Export
    public boolean isPending() {
        return getState().equals(State.LOADING);
    }

    @Export
    public String poop() {
        return "POOP";
    }

    @Export
    public ThreedSession getThreedSession1() {
        return (ThreedSession) result;
    }

    @Export
    public S getThreedSession2() {
        return result;
    }

}
