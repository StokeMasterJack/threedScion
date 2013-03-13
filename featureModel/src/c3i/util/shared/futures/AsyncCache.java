package c3i.util.shared.futures;

public interface AsyncCache<K, V> {

    Future<V> get(K key);

}
