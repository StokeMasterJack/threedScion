package c3i.util.shared.futures;

public interface KeyGetter<K, V> {

    K getKey(V value);
}
