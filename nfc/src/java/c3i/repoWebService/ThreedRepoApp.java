package c3i.repoWebService;

import smartsoft.util.config.App;
import smartsoft.util.lang.shared.Path;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.Map;

public class ThreedRepoApp extends App {

    private static final String REPO_BASE_DIR_KEY = "repoBaseDir";
    private static final String REPO_BASE_URL_KEY = "repoBaseUrl";

    private static final File REPO_BASE_DIR_SHARE = new File("/www_share/nfc_image_repo");
    private static final File REPO_BASE_DIR_PRIVATE = new File("/configurator-content");


    public ThreedRepoApp() {
        super("threed-repo");
    }

    public Map<String, String> getRepoBaseDirs() {
        return getProperties(REPO_BASE_DIR_KEY);
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

    public static ThreedRepoApp getFromServletContext(ServletContext servletContext) {
        return (ThreedRepoApp) servletContext.getAttribute(ThreedRepoApp.class.getName());
    }


}
