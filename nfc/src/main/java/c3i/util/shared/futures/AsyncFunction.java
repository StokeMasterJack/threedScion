package c3i.util.shared.futures;

public interface AsyncFunction<I, T> {

    void start(final I arg, final Completer<T> completer) throws Exception;

}
