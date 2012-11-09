package c3i.admin.server.taskManager;

import c3i.admin.server.JpgSet;
import c3i.repo.server.Repos;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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


    private static Log log = LogFactory.getLog(JpgSetAction.class);


}
