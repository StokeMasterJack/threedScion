package c3i.repo.server.rt;

import c3i.featureModel.shared.common.RootTreeId;
import c3i.featureModel.shared.common.SeriesKey;
import c3i.imageModel.server.ImageUtil;
import c3i.imageModel.shared.BaseImageType;
import c3i.imageModel.shared.IBaseImageKey;
import c3i.imageModel.shared.ImView;
import c3i.imageModel.shared.PngSegment;
import c3i.imageModel.shared.Profile;
import c3i.imageModel.shared.Slice;
import c3i.repo.server.TwoThirty8;
import c3i.threedModel.client.ThreedModel;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectStream;
import smartsoft.util.FileUtil;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Logger;

/**
 * This is the gen directory
 */
public class RtRepo  {

    private final File rtRepoDir;
    private final SeriesKey seriesKey;

    private final File jpgDir;
    private final File pngDir;

    private final File modelsDir;
    private final File commitsDir;
    private final File pngInfoDir;
    private final File emptyPngDir;
    private final File notEmptyPngDir;
    private final File versionsDir;
    private final File cacheDir;

    public RtRepo(File rtRepoDir, SeriesKey seriesKey) {
        assert rtRepoDir != null;
        assert seriesKey != null;

        this.rtRepoDir = rtRepoDir;
        this.seriesKey = seriesKey;

        FileUtil.createDirNotExists(rtRepoDir);

        this.modelsDir = new File(rtRepoDir, "models");
        FileUtil.createDirNotExists(modelsDir);

        this.commitsDir = new File(rtRepoDir, "commits");
        FileUtil.createDirNotExists(commitsDir);

        this.jpgDir = new File(rtRepoDir, "jpgs");
        FileUtil.createDirNotExists(jpgDir);

        this.pngDir = new File(rtRepoDir, "zPngs");
        FileUtil.createDirNotExists(pngDir);

        this.pngInfoDir = new File(rtRepoDir, "png-info");
        FileUtil.createDirNotExists(pngInfoDir);

        emptyPngDir = new File(pngInfoDir, "empty");
        FileUtil.createDirNotExists(emptyPngDir);

        notEmptyPngDir = new File(pngInfoDir, "not-empty");
        FileUtil.createDirNotExists(notEmptyPngDir);

        versionsDir = new File(this.rtRepoDir, "versions");
        FileUtil.createDirNotExists(versionsDir);

        cacheDir = new File(this.rtRepoDir, "cache");
        FileUtil.createDirNotExists(cacheDir);

    }

    public File getJpgByLongIdOld(String longId) {
        String shortId = ImageUtil.getFingerprint(longId);
        return getJpgByShortId(shortId);
    }

    public File getJpgByShortId(String shortId) {
        String twoDigitDirName = shortId.substring(0, 2);
        String fileName = shortId.substring(2) + ".jpg";
        File twoDigitDir = new File(jpgDir, twoDigitDirName);
        return new File(twoDigitDir, fileName);
    }

    //                    ObjectLoader loader = repo.open(objectId);
//                    is = loader.openStream();
//                    if (ImageUtil.allPixesHaveAlphaZero(is)) {
//                        Files.touch(emptyPngFile);
//                        return new PngInfo(true);
//                    } else {
//                        Files.touch(notEmptyPngFile);
//                        return new PngInfo(false);
//                    }


    private static Logger log = Logger.getLogger("c3i");


    public boolean isEmptyPng(String fullFileName, ObjectId fullPngSha, InputSupplier<? extends InputStream> content) {
        ObjectStream is = null;
        try {

            File emptyPngFile = new File(emptyPngDir, fullPngSha.getName());
            File notEmptyPngFile = new File(notEmptyPngDir, fullPngSha.getName());

            if (emptyPngFile.exists()) {
                log.fine("emptyPngFile exists for[" + fullFileName + "]");
                return true;
            } else if (notEmptyPngFile.exists()) {
                return false;
            } else {
                boolean emptyPng = ImageUtil.isEmptyPng(fullFileName, content);

                if (emptyPng) {
                    log.fine("ImageUtil.isEmptyPng for[" + fullFileName + "]");
                    Files.createParentDirs(emptyPngFile);
                    Files.touch(emptyPngFile);
                    return true;
                } else {
                    Files.createParentDirs(notEmptyPngFile);
                    Files.touch(notEmptyPngFile);
                    return false;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            Closeables.closeQuietly(is);
        }
    }

    public File getJpgDir() {
        return jpgDir;
    }

    public File getJpgDirForSize(@Nonnull Profile profile) {
        return new File(jpgDir, profile.getKey());
    }

    public File getPngDir() {
        return pngDir;
    }

    public File getBaseImageFileName(IBaseImageKey jpgKey) {
        Profile profile = jpgKey.getProfile();
        BaseImageType baseImageType = profile.getBaseImageType();
        if (baseImageType == null) {
            throw new IllegalStateException();
        }
        File jpgDirForSize = getJpgDirForSize(profile);
        String fingerprint = jpgKey.getFingerprint();
        TwoThirty8 twoThirty8 = TwoThirty8.getTwoThirty8(fingerprint);
        File jpgFileName = twoThirty8.getFileName(jpgDirForSize, baseImageType);
        return jpgFileName;
    }

    public boolean exists(IBaseImageKey jpgKey) {
        File f = getBaseImageFileName(jpgKey);
        return f.exists();
    }

    public File getVersionsDir() {
        return versionsDir;
    }

    public File getVersionDir(RootTreeId rootTreeId) {
        return new File(getVersionsDir(), rootTreeId.stringValue());
    }

    public File getVersionWidthDir(RootTreeId rootTreeId, Profile profile) {
        File versionDir = getVersionDir(rootTreeId);
        return new File(versionDir, profile.getKey());
    }

    public File getVersionSliceDir(RootTreeId rootTreeId, ImView view, int angle) {
        String sliceName = view.getName() + "-" + Slice.getAnglePadded(angle);
        return new File(getVersionDir(rootTreeId), sliceName);
    }

    public File getVersionWidthSliceDir(RootTreeId rootTreeId, Profile profile, ImView view, int angle) {
        String sliceName = view.getName() + "-" + Slice.getAnglePadded(angle);
        return new File(getVersionWidthDir(rootTreeId, profile), sliceName);
    }

    public File getCacheDir() {
        return cacheDir;
    }

    public File getJpgSetFile(RootTreeId rootTreeId, ImView view, int angle) {
        File vws = getVersionSliceDir(rootTreeId, view, angle);
        return new File(vws, "jpgSet.ser");
    }

    public File getJpgJobStartedFile(RootTreeId rootTreeId, Profile profile, ImView view, int angle) {
        File vws = getVersionWidthSliceDir(rootTreeId, profile, view, angle);
        return new File(vws, "started.txt");
    }

    public File getJpgJobCompleteFile(RootTreeId rootTreeId, Profile profile, ImView view, int angle) {
        File vws = getVersionWidthSliceDir(rootTreeId, profile, view, angle);
        return new File(vws, "complete.txt");
    }

    public File getJpgCountFile(RootTreeId rootTreeId, ImView view, int angle) {
        File vws = getVersionSliceDir(rootTreeId, view, angle);
        return new File(vws, "jpgCount.txt");
    }

    public void createJpgGenCacheDir(RootTreeId rootTreeId, ThreedModel threedModel, Profile profile) {

        File versionWidthDir = getVersionWidthDir(rootTreeId, profile);

        File startedFile = new File(versionWidthDir, "started.txt");
        if (!startedFile.exists()) {
            try {
                Files.createParentDirs(startedFile);
                boolean newFile = startedFile.createNewFile();
                if (newFile) {
                    Files.write(System.currentTimeMillis() + "", startedFile, Charset.defaultCharset());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        for (ImView view : threedModel.getViews()) {
            List<Integer> angles = view.getAngles();
            for (Integer angle : angles) {
                File file = this.getVersionWidthSliceDir(rootTreeId, profile, view, angle);
                if (!file.exists()) {
                    try {
                        Files.createParentDirs(file);
                        file.mkdir();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

    }

    public File getZPngFileName(PngSegment pngKey) {
        File pngDir = getPngDir();
        String fingerprint = pngKey.serializeToUrlSegment();
        File pngFileName = new File(pngDir, fingerprint + ".png");
        return pngFileName;
    }
}
