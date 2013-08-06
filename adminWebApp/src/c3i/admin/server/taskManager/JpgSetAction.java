package c3i.admin.server.taskManager;

import c3i.admin.server.JpgSet;
import c3i.repo.server.Repos;

import java.util.logging.Logger;


import java.io.File;

/**
 * This class computes the list of jpgs for one slice.
 * Depends on: SeriesId seriesId + slice
 * Does not not depend on profile
 */
public class JpgSetAction {

    private final Repos repos;
    private final JpgSet.JpgSetKey jpgSetKey;
    private final File jpgSetFile;

    private JpgSet jpgSet;

    public JpgSetAction(Repos repos, JpgSet.JpgSetKey jpgSetKey) {
        log.info("JpgSetAction(" + jpgSetKey + ")");
        this.repos = repos;
        this.jpgSetKey = jpgSetKey;
        jpgSetFile = jpgSetKey.getFileName(repos.getCacheDir());
        log.info("jpgSetFile: " + jpgSetFile);
    }

    public JpgSet getJpgSet() {
        if (this.jpgSet == null) {
            if (jpgSetFileExists()) {
                log.info("jpgSetFile[" + jpgSetFile + "] exists. Reading jpgSet from file...");
                this.jpgSet = JpgSet.readJpgSetFile(repos.getCacheDir(), jpgSetKey);
                log.info("jpgSetFile[" + jpgSetFile + "] read complete");
            } else {
                log.info("jpgSetFile[" + jpgSetFile + "] does not exist. Creating jpgSet.");
                this.jpgSet = JpgSet.createJpgSet(repos, jpgSetKey);
                log.info("jpgSetFile[" + jpgSetFile + "] create complete");
                log.info("jpgSet created!");
                log.info("Caching jpgSet for future use as jpgSetFile[" + jpgSetFile + "]");
                jpgSet.writeToFile(repos.getCacheDir());
                log.info("jpgSet cached as jpgSetFile[" + jpgSetFile + "]");
            }
        }

        log.info("Returning jpgSet with size: " + jpgSet.size());
        return jpgSet;
    }


    private void pukeIfCanceled() {
        //todo
    }

    public boolean jpgSetFileExists() {
        return jpgSetFile.exists();
    }


    private static Logger log = Logger.getLogger("c3i");


}
