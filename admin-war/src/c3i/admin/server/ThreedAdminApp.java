package c3i.admin.server;

import c3i.core.common.shared.BrandKey;
import c3i.imgGen.server.taskManager.JpgGeneratorService;
import c3i.repo.server.BrandRepos;
import com.google.common.collect.ImmutableMap;
import smartsoft.util.config.App;
import smartsoft.util.shared.Path;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.Map;

public class ThreedAdminApp extends App {

    private static final String REPO_BASE_DIR_KEY = "repoBaseDir";
    private static final String REPO_CONTEXT_PATH_KEY = "repoContextPath";

    private static final File REPO_BASE_DIR_SHARE = new File("/www_share/nfc_image_repo");
    private static final File REPO_BASE_DIR_PRIVATE = new File("/configurator-content");

    private BrandRepos brandRepos;
    private JpgGeneratorService jpgGeneratorService;

    public ThreedAdminApp() {
        super("threed-admin");
        brandRepos = new BrandRepos(getRepoBaseDirs());
        jpgGeneratorService = new JpgGeneratorService(brandRepos);
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

    public String getRepoBaseDirName(String brand) {
        String propName = brand + "." + REPO_BASE_DIR_KEY;
        return getProperty(propName);
    }

    public String getRepoContextPathName() {
        return getProperty(REPO_CONTEXT_PATH_KEY);
    }

    public Path getRepoContextPath() {
        return new Path(getRepoContextPathName());
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

    public synchronized static ThreedAdminApp getFromServletContext(ServletContext servletContext) {
        ThreedAdminApp app = (ThreedAdminApp) servletContext.getAttribute(ThreedAdminApp.class.getName());
        if (app == null) {
            app = new ThreedAdminApp();
            servletContext.setAttribute(ThreedAdminApp.class.getName(), app);
        }
        return app;
    }

    public BrandRepos getBrandRepos() {
        return brandRepos;
    }

    public JpgGeneratorService getJpgGeneratorService() {
        return jpgGeneratorService;
    }
}
