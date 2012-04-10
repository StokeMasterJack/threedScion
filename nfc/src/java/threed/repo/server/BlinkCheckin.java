package threed.repo.server;

import com.google.common.io.Files;
import threed.core.imageModel.shared.PngShortSha;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.io.IOException;

public class BlinkCheckin {

    public static void processBlinks(Repository repository, RevCommit revCommit) throws IOException {
        log.info("Setting up blinks");

        File workDir = repository.getWorkTree();
        File blinksDir = new File(repository.getDirectory(), ".gen/blinks");

        if (!blinksDir.isDirectory()) {
            blinksDir.mkdir();
        }

        indexBlinks(repository, blinksDir, workDir, revCommit);
    }

    private static void indexBlinks(Repository repository, File blinksDir, File workDir, RevCommit revCommit) throws IOException {
        log.debug("Indexing blinks from dir [" + workDir + "]");
        for (File file : workDir.listFiles()) {
            if (file.isDirectory() && !file.getName().contains(".git")) {
                indexBlinks(repository, blinksDir, file, revCommit);
            } else if (file.getName().endsWith("_w.png")) {
                log.debug("Found blink png [" + file + "]");
                saveBlink(repository, blinksDir, revCommit, file);
            }
        }

    }

    private static void saveBlink(Repository repository, File blinksDir, RevCommit revCommit, File wPngFileFromWork) throws IOException {
        String stripW = stripW(wPngFileFromWork.getAbsolutePath());
        String relativePath = stripW.substring(repository.getWorkTree().getAbsolutePath().length() + 1);

        char char1 = '\\';
        char char2 = '/';
        relativePath = relativePath.replace(char1, char2);

        String revisionParameter = revCommit.getId().getName() + ":" + relativePath;
        ObjectId resolved = repository.resolve(revisionParameter);
        if (resolved != null) {

            String pngFullSha = resolved.getName();
            PngShortSha pngShortSha = new PngShortSha(pngFullSha);

            File newCopy = new File(blinksDir, pngShortSha.stringValue() + ".png");

            if (!newCopy.exists()) {
                log.debug("Copying blink png from [" + wPngFileFromWork + "] to [" + newCopy + "]");
                Files.copy(wPngFileFromWork, newCopy);
            }
        } else {
            log.error("Could not resolve blink png revisionParameter [" + revisionParameter + "]");
        }
    }

    private static String stripW(String in) {
        return in.substring(0, in.length() - 6) + ".png";
    }

    private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(BlinkCheckin.class);
}
