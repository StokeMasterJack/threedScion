package c3i.admin.server;

import smartsoft.util.config.App;
import smartsoft.util.lang.shared.Path;

import java.io.File;

public class ThreedAdminApp extends App {

    private static final String REPO_BASE_DIR_KEY = "repoBaseDir";
    private static final String REPO_CONTEXT_PATH_KEY = "repoContextPath";

    private static final File REPO_BASE_DIR_SHARE = new File("/www_share/nfc_image_repo");
    private static final File REPO_BASE_DIR_PRIVATE = new File("/configurator-content");

    private static ThreedAdminApp INSTANCE;

    private ThreedAdminApp() {
        super("threed-admin");
    }

    public static ThreedAdminApp get() {
        if (INSTANCE == null) {
            INSTANCE = new ThreedAdminApp();
        }
        return INSTANCE;
    }

    public String getRepoBaseDirName() {
        return getProperty(REPO_BASE_DIR_KEY);
    }

    public String getRepoContextPathName() {
        return getProperty(REPO_CONTEXT_PATH_KEY);
    }

    public Path getRepoContextPath() {
        return new Path(getRepoContextPathName());
    }

    public File getRepoBaseDir() {
        String dirName = getRepoBaseDirName();
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


}
