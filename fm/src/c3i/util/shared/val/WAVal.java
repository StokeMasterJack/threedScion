package c3i.util.shared.val;

public interface WAVal<T> extends WVal<T>, AVal<T>, Completer<T> {

    void start();

}
