package c3i.imgGen.repoImpl;

import c3i.imgGen.api.FeatureModelFactory;
import c3i.imgGen.api.ImageModelFactory;
import c3i.imgGen.api.Kit;
import c3i.imgGen.api.SrcPngLoader;
import c3i.repo.server.BrandRepos;

public class KitRepo implements Kit {

    private final BrandRepos brandRepos;

    private final FeatureModelFactory featureModelFactory;
    private final ImageModelFactory imageModelFactory;
    private final SrcPngLoader srcPngLoader;

    public KitRepo(BrandRepos brandRepos) {
        this.brandRepos = brandRepos;
        featureModelFactory = new FeatureModelFactoryRepo(brandRepos);
        imageModelFactory = new ImageModelFactoryRepo(brandRepos);
        srcPngLoader = new SrcPngLoaderRepo(brandRepos);
    }

    @Override
    public FeatureModelFactory createFeatureModelFactory() {
        return featureModelFactory;
    }

    @Override
    public ImageModelFactory createImageModelFactory() {
        return imageModelFactory;
    }

    @Override
    public SrcPngLoader createSrcPngLoader() {
        return srcPngLoader;
    }
}
