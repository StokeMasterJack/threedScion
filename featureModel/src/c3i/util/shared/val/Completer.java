package c3i.util.shared.val;

public interface Completer<T> {

    void set(T completedValue);

    void setException(Throwable e);

}
