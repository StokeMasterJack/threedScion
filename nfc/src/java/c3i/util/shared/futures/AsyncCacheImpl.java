package c3i.util.shared.futures;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class AsyncCacheImpl<K, V> implements AsyncCache<K, V> {

    private final AsyncFunction<K, V> asyncFunction;
    private final Cache<K, Loader<K, V>> cache;

    public AsyncCacheImpl(AsyncFunction<K, V> asyncFunction) {
        this.asyncFunction = asyncFunction;
        cache = CacheBuilder
                .newBuilder()
                .maximumSize(1000)
                .build();
    }

    @Override
    public Future<V> get(K key) {
        Loader<K, V> loader = cache.getIfPresent(key);
        if (loader == null) {
            loader = new Loader<K, V>(key, asyncFunction);
            cache.put(key, loader);
        }
        return loader.ensureLoaded();
    }

}
