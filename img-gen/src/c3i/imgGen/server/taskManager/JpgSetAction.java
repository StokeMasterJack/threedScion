package c3i.imgGen.server.taskManager;

import c3i.imgGen.server.JpgSet;
import c3i.repo.server.Repos;

import java.io.File;
import java.util.logging.Logger;

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
        this.repos = repos;
        this.jpgSetKey = jpgSetKey;
        jpgSetFile = jpgSetKey.getFileName(repos.getCacheDir());
    }


    public JpgSet getJpgSet() {
        if (this.jpgSet == null) {
            if (jpgSetFileExists()) {
                this.jpgSet = JpgSet.readJpgSetFile(repos.getCacheDir(), jpgSetKey);
            } else {
                this.jpgSet = JpgSet.createJpgSet(repos, jpgSetKey);
                jpgSet.writeToFile(repos.getCacheDir());
            }
        }
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
