package c3i.util.shared.futures;

public class CompleterImpl<T> implements Completer<T> {

    final FutureImpl<T> futureImpl;

    public CompleterImpl() {
        futureImpl = new FutureImpl();
    }

    public Future<T> getFuture() {
        return futureImpl;
    }

    public void setResult(T value) {
        futureImpl._setValue(value);
    }

    public void setException(Throwable exception) {
        futureImpl._setException(exception);
    }
}
