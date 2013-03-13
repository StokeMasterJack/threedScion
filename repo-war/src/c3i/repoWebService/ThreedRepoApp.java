package c3i.repoWebService;

import c3i.featureModel.shared.common.BrandKey;
import c3i.featureModel.shared.common.SeriesId;
import c3i.imgGen.api.Kit;
import c3i.imgGen.api.SrcPngLoader;
import c3i.imgGen.repoImpl.KitRepo;
import c3i.repo.server.BrandRepos;
import com.google.common.collect.ImmutableMap;
import smartsoft.util.config.App;
import smartsoft.util.shared.Path;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.Map;

public class ThreedRepoApp extends App {

    private static final String REPO_BASE_DIR_KEY = "repoBaseDir";
    private static final String REPO_BASE_URL_KEY = "repoBaseUrl";

    private static final File REPO_BASE_DIR_SHARE = new File("/www_share/nfc_image_repo");
    private static final File REPO_BASE_DIR_PRIVATE = new File("/configurator-content");

    private BrandRepos brandRepos;
    private Kit<SeriesId> kit;
    private SrcPngLoader pngLoader;

    public ThreedRepoApp() {
        super("threed-repo");
        brandRepos = new BrandRepos(getRepoBaseDirs());
        kit = new KitRepo(brandRepos);
        pngLoader = kit.createSrcPngLoader();
    }

    public ImmutableMap<BrandKey, File> getRepoBaseDirs() {
        Map<String, String> m = getProperties(REPO_BASE_DIR_KEY);

        ImmutableMap.Builder<BrandKey, File> b = ImmutableMap.builder();
        for (String brand : m.keySet()) {
            BrandKey brandKey = BrandKey.fromString(brand);
            String sFile = m.get(brand);
            File f = new File(sFile);
            b.put(brandKey, f);
        }

        return b.build();
    }

    public SrcPngLoader getPngLoader() {
        return pngLoader;
    }

    public String getRepoBaseDirName(String brand) {
        String propName = brand + "." + REPO_BASE_DIR_KEY;
        return getProperty(propName);
    }

    public String getRepoBaseUrlName() {
        return getProperty(REPO_BASE_URL_KEY);
    }

    public Path getRepoBaseUrl() {
        return new Path(getRepoBaseUrlName());
    }

    public File getRepoBaseDir(String brand) {
        String dirName = getRepoBaseDirName(brand);
        log.info("Read repoBaseDir[" + dirName + "] from properties file");

        if (dirName != null) {
            return new File(dirName);
        }

        log.info("No repoBaseDir found in properties file, trying [" + REPO_BASE_DIR_SHARE + "]");
        if (REPO_BASE_DIR_SHARE.exists()) {
            return REPO_BASE_DIR_SHARE;
        }

        log.info(REPO_BASE_DIR_SHARE + " does not exist, trying [" + REPO_BASE_DIR_PRIVATE + "]");
        if (REPO_BASE_DIR_PRIVATE.exists()) {
            return REPO_BASE_DIR_PRIVATE;
        }

        log.info(REPO_BASE_DIR_PRIVATE + " does not exist. Nothing more to try");
        throw new IllegalStateException("Could not find " + REPO_BASE_DIR_KEY);


    }

    public synchronized static ThreedRepoApp getFromServletContext(ServletContext servletContext) {
        ThreedRepoApp app = (ThreedRepoApp) servletContext.getAttribute(ThreedRepoApp.class.getName());
        if (app == null) {
            app = new ThreedRepoApp();
            servletContext.setAttribute(ThreedRepoApp.class.getName(), app);
        }
        return app;
    }

    public BrandRepos getBrandRepos() {
        return brandRepos;
    }


}
