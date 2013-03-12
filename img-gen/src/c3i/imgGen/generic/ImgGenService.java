package c3i.imgGen.generic;

import c3i.core.featureModel.shared.FeatureModel;
import c3i.imageModel.shared.ImageModel;
import c3i.imgGen.api.FeatureModelFactory;
import c3i.imgGen.api.ImageModelFactory;
import c3i.imgGen.api.Kit;
import c3i.imgGen.repoImpl.FmIm;
import c3i.imgGen.server.JpgSet;
import c3i.imgGen.server.JpgSetTask;
import c3i.imgGen.server.JpgSets;
import c3i.imgGen.server.JpgSetsTask;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;

public class ImgGenService<ID> {

    private final LoadingCache<ID, FmIm> fmImCache;

    private final FeatureModelFactory fmFactory;
    private final ImageModelFactory imFactory;

    private final Kit<ID> kit;

    public ImgGenService(Kit<ID> kit) {

        this.kit = kit;

        fmFactory = kit.createFeatureModelFactory();
        imFactory = kit.createImageModelFactory();

        fmImCache = CacheBuilder.newBuilder()
                .concurrencyLevel(4)
                .maximumSize(1000)
                .build(
                        new CacheLoader<ID, FmIm>() {
                            @Override
                            public FmIm load(ID contextId) throws Exception {
                                FeatureModel fm = fmFactory.createFeatureModel(contextId);
                                ImageModel im = imFactory.createImageModel(fm, contextId);
                                return new FmIm(contextId, fm, im);
                            }
                        });

    }

    public FmIm getFmIm(ID id) {
        try {
            return fmImCache.get(id);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    public JpgSet getJpgSet(ID id, String viewName, int angle) {
        FmIm fmIm = getFmIm(id);
        JpgSetTask task = new JpgSetTask(fmIm, viewName, angle);
        task.start();
        return task.getJpgSet();
    }

    public JpgSets getJpgSets(ID id) {
        FmIm fmIm = getFmIm(id);
        JpgSetsTask task = new JpgSetsTask(fmIm);
        task.start();
        return task.getJpgSets();
    }

}
