package c3i.util.shared.val;

public interface AsyncLoader<T> {

    void start(Completer<T> completer);

}
