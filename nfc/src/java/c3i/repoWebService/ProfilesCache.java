package c3i.repoWebService;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import c3i.core.common.shared.BrandKey;
import c3i.core.imageModel.shared.Profile;
import c3i.core.imageModel.shared.Profiles;
import c3i.repo.server.Repos;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ProfilesCache {

    private final LoadingCache<BrandKey, Profiles> cache;

    private ProfilesCache() {

        cache = CacheBuilder.newBuilder()
                .concurrencyLevel(4)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build(
                        new CacheLoader<BrandKey, Profiles>() {
                            @Override
                            public Profiles load(BrandKey brandKey) throws Exception {
                                return Repos.get().getProfiles(brandKey);
                            }
                        });

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

    private final static ProfilesCache INSTANCE = new ProfilesCache();

    public static ProfilesCache get() {
        return INSTANCE;
    }
}
