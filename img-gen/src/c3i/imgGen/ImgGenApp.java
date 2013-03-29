package c3i.imgGen;

import c3i.imgGen.api.FeatureModelFactory;
import c3i.imgGen.api.ImageModelFactory;
import c3i.imgGen.api.ThreedModelService;
import c3i.imgGen.generic.ThreedModelServiceRepo;
import c3i.imgGen.repoImpl.FeatureModelFactoryRepo;
import c3i.imgGen.repoImpl.ImageModelFactoryRepo;
import c3i.ip.SrcPngLoader;
import c3i.repo.server.SrcPngLoaderRepo;
import c3i.imgGen.repoImpl.ThreedModelFactoryRepo;
import c3i.imgGen.server.taskManager.JpgGeneratorService;
import c3i.repo.ReposConfig;
import c3i.repo.server.BrandRepos;

import java.io.File;

public class ImgGenApp {

    private ReposConfig reposConfig;

    private FeatureModelFactory featureModelFactory;
    private ImageModelFactory imageModelFactory;
    private SrcPngLoader srcPngLoader;


    private BrandRepos brandRepos;

    private ThreedModelService threedModelService;
    private JpgGeneratorService jpgGeneratorService;


    public ImgGenApp() {
        this(ReposConfig.testRepos());
    }

    public ImgGenApp(File... repoBaseDirs) {
        this(new ReposConfig(repoBaseDirs));
    }

    public ImgGenApp(ReposConfig reposConfig) {
        this.reposConfig = reposConfig;
    }

    public BrandRepos getBrandRepos() {
        if (brandRepos == null) {
            brandRepos = createBrandRepos();
        }
        return brandRepos;
    }


    public ThreedModelService getThreedModelService() {
        if (threedModelService == null) {
            threedModelService = new ThreedModelServiceRepo(createThreedModelFactory());
        }
        return threedModelService;
    }


    public JpgGeneratorService getJpgGeneratorService() {
        if (jpgGeneratorService == null) {
            jpgGeneratorService = createJpgGeneratorService();
        }
        return jpgGeneratorService;
    }

    public SrcPngLoader getSrcPngLoader() {
        if (srcPngLoader == null) {
            srcPngLoader = createSrcPngLoader();
        }
        return srcPngLoader;
    }

    public ImageModelFactory getImageModelFactory() {
        if (imageModelFactory == null) {
            imageModelFactory = createImageModelFactory();
        }
        return imageModelFactory;
    }


    public FeatureModelFactory getFeatureModelFactory() {
        if (featureModelFactory == null) {
            featureModelFactory = createFeatureModelFactory();
        }
        return featureModelFactory;
    }


    protected BrandRepos createBrandRepos() {
        return new BrandRepos(reposConfig);
    }

    protected JpgGeneratorService createJpgGeneratorService() {
        return new JpgGeneratorService(
                getBrandRepos(),
                (ThreedModelServiceRepo) getThreedModelService(),
                getSrcPngLoader()
        );
    }

    protected FeatureModelFactory createFeatureModelFactory() {
        return new FeatureModelFactoryRepo(getBrandRepos());
    }

    protected ImageModelFactory createImageModelFactory() {
        return new ImageModelFactoryRepo(getBrandRepos());
    }

    private SrcPngLoader createSrcPngLoader() {
        return new SrcPngLoaderRepo(getBrandRepos());
    }


    private ThreedModelFactoryRepo createThreedModelFactory() {
        return new ThreedModelFactoryRepo(getFeatureModelFactory(), getImageModelFactory());
    }

}
