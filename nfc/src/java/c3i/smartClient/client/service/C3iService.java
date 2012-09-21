package c3i.smartClient.client.service;

import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesId;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.featureModel.shared.FixedPicks;
import c3i.core.imageModel.shared.ImView;
import c3i.core.imageModel.shared.RawImageStack;
import c3i.core.threedModel.shared.Brand;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.util.shared.futures.AsyncCache;
import c3i.util.shared.futures.AsyncCacheImpl;
import c3i.util.shared.futures.AsyncFunction;
import c3i.util.shared.futures.Completer;
import c3i.util.shared.futures.Future;
import c3i.util.shared.futures.OnSuccess;
import smartsoft.util.lang.shared.Path;

import javax.annotation.Nonnull;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class C3iService {

    private final Path repoBaseUrl;
    private final AsyncCache<BrandKey, Brand> brandCache;
    private final AsyncCache<SeriesId, ThreedModel> threedModelCache;


    private final AsyncCache<ImageStackRequest, RawImageStack> rawImageCache;
    private final AsyncCache<Set<String>, FixedPicks> fixedPicksCache;

    private final ThreedModelClient threedModelClient;

    public C3iService(Path repoBaseUrl) {
        checkNotNull(repoBaseUrl);
        this.repoBaseUrl = repoBaseUrl;

        threedModelClient = new ThreedModelClient(repoBaseUrl);

        brandCache = new AsyncCacheImpl<BrandKey, Brand>(threedModelClient.brandLoaderFunction);

        rawImageCache = new AsyncCacheImpl<ImageStackRequest, RawImageStack>(new AsyncFunction<ImageStackRequest, RawImageStack>() {
            @Override
            public void start(final ImageStackRequest request, Completer<RawImageStack> completer) throws RuntimeException {
                SeriesId seriesId = request.getSeriesId();


                threedModelCache.get(seriesId).success(new OnSuccess<ThreedModel>() {
                    @Override
                    public void onSuccess(@Nonnull ThreedModel threedModel) {
                        String viewName = request.getView();
                        ImView view = threedModel.getView(viewName);
//                        view.getRawImageStack(new FixedPicks(), )
                    }
                });


            }
        });

        threedModelCache=new AsyncCacheImpl<SeriesId, ThreedModel>(new AsyncFunction<SeriesId, ThreedModel>() {
            @Override
            public void start(SeriesId input, Completer<ThreedModel> threedModelCompleter) throws RuntimeException {

            }
        });

        fixedPicksCache = new AsyncCacheImpl<Set<String>, FixedPicks>(new AsyncFunction<Set<String>, FixedPicks>() {
            @Override
            public void start(Set<String> input, Completer<FixedPicks> fixedPicksCompleter) throws RuntimeException {

            }
        });


    }

    private AsyncCache<Set<String>, FixedPicks> createFixedPicksCache(final Future<ThreedModel> threedModelFuture) {
        return new AsyncCacheImpl<Set<String>, FixedPicks>(new AsyncFunction<Set<String>, FixedPicks>() {
            @Override
            public void start(final Set<String> rawPicks, final Completer<FixedPicks> fixedPicksCompleter) throws RuntimeException {
                threedModelFuture.success(new OnSuccess<ThreedModel>() {
                    @Override
                    public void onSuccess(@Nonnull ThreedModel threedModel) {
                        FeatureModel featureModel = threedModel.getFeatureModel();
                        FixedPicks fixedPicks = featureModel.fixupRaw(rawPicks);
                        fixedPicksCompleter.setResult(fixedPicks);
                    }
                });
            }
        });
    }

    public Future<RawImageStack> getImageStack(ImageStackRequest request) {
        SeriesId seriesId = request.getSeriesId();

        //(ValidFixedPicks + viewIndex:int + angle:int)  => List<SourcePng>

        return null;
    }


}
