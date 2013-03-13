package c3i.util.shared.val;

public interface AsyncFunction<I, V> {

    void start(I key, Completer<V> completer);

}
