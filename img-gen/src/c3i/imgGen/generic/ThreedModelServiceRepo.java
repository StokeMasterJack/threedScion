package c3i.imgGen.generic;

import c3i.featureModel.shared.common.SeriesId;
import c3i.imageModel.shared.Slice;
import c3i.imgGen.api.ThreedModelService;
import c3i.imgGen.repoImpl.ThreedModelFactoryRepo;
import c3i.threedModel.shared.JpgSet;
import c3i.threedModel.shared.JpgSetTask;
import c3i.threedModel.shared.JpgSets;
import c3i.threedModel.shared.JpgSetsTask;
import c3i.threedModel.shared.ThreedModel;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;

public class ThreedModelServiceRepo implements ThreedModelService<SeriesId> {

    private final LoadingCache<SeriesId, ThreedModel> threedModelCache;


    public ThreedModelServiceRepo(final ThreedModelFactoryRepo threedModelFactory) {

        threedModelCache = CacheBuilder.newBuilder()
                .concurrencyLevel(4)
                .maximumSize(1000)
                .build(
                        new CacheLoader<SeriesId, ThreedModel>() {
                            @Override
                            public ThreedModel load(SeriesId id) throws Exception {
                                return threedModelFactory.createThreedModel(id);
                            }
                        });

    }

    @Override
    public ThreedModel getThreedModel(SeriesId id) {
        try {
            return threedModelCache.get(id);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public JpgSetTask createJpgSetTask(SeriesId id, Slice slice) {
        ThreedModel threedModel = getThreedModel(id);
        return threedModel.createJpgSetTask(slice);
    }

    public JpgSet buildJpgSet(SeriesId id, Slice slice) {
        JpgSetTask task = createJpgSetTask(id, slice);
        task.start();
        return task.getJpgSet();
    }

    public JpgSetsTask createJpgSetsTask(SeriesId id) {
        ThreedModel threedModel = getThreedModel(id);
        return threedModel.createJpgSetsTask();
    }

//    public JpgSets buildJpgSets(SeriesId id) {
//        JpgSetsTask task = createJpgSetsTask(id);
//        task.start();
//        return task.getJpgSets();
//    }

    public JpgSets buildJpgSets(SeriesId id) {
        ThreedModel threedModel = getThreedModel(id);
        JpgSetsTask task = new JpgSetsTask(threedModel);
        task.start();
        return task.getJpgSets();
    }

}
