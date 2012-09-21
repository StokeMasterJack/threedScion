package c3i.util.shared.futures;

public interface OnFailureFactory {

    OnException createOnFailure(Future future,Throwable exception);
}
