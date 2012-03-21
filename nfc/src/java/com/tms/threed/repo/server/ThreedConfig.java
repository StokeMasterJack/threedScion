package com.tms.threed.repo.server;

import smartsoft.util.config.ConfigHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

public class ThreedConfig extends ConfigHelper {

    public static final String REPO_BASE_DIR_KEY = "repoBaseDir";
    public static final String REPO_BASE_URL_KEY = "repoBaseUrl";
    public static final String VTC_BASE_DIR_KEY = "vtcDir";



    private static final File REPO_BASE_DIR_SHARE = new File("/www_share/nfc_image_repo");
    private static final File REPO_BASE_DIR_PRIVATE = new File("/configurator-content");

    public static String getRepoBaseDirName() {
        return getProperty(REPO_BASE_DIR_KEY);
    }


    public static String getRepoBaseUrlName() {
        return getProperty(REPO_BASE_URL_KEY);
    }

    public static File getRepoBaseDir() {
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




    //DEAD
    public static String getVtcDirName() {
        return getProperty(VTC_BASE_DIR_KEY);
    }

    private static Log log = LogFactory.getLog(ThreedConfig.class);

}
