package c3i.repo.server;

import c3i.featureModel.shared.common.BrandKey;
import c3i.imageModel.shared.Profile;
import c3i.imageModel.shared.Profiles;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ProfilesCache {

    private final LoadingCache<BrandKey, Profiles> cache;

    public ProfilesCache(CacheLoader<BrandKey, Profiles> cacheLoader) {

        cache = CacheBuilder.newBuilder()
                .concurrencyLevel(4)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build(cacheLoader);
    }

    public Profile getProfile(final BrandKey brandKey, String profileKey) {
        return getProfiles(brandKey).get(profileKey);
    }

    public Profiles getProfiles(final BrandKey brandKey) {
        try {
            return cache.get(brandKey);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


}
