package c3i.util.shared.futures;

public interface FutureCompleter<T> extends Completer<T> {

    Future<T> getFuture();
}
