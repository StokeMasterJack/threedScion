package com.tms.threed.repo.server.rt;

import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.tms.threed.repo.server.TwoThirty8;
import com.tms.threed.repo.shared.JpgKey;
import com.tms.threed.threedCore.imageModel.server.BlinkChecker;
import com.tms.threed.threedCore.imageModel.server.ImageUtil;
import com.tms.threed.threedCore.imageModel.shared.PngShortSha;
import com.tms.threed.threedCore.threedModel.shared.JpgWidth;
import com.tms.threed.threedCore.threedModel.shared.RootTreeId;
import smartsoft.util.FileUtil;
import com.tms.threed.threedCore.threedModel.shared.SeriesKey;
import com.tms.threed.threedCore.threedModel.shared.Slice;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectStream;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

public class RtRepo implements BlinkChecker {

    private final File rtRepoDir;
    private final SeriesKey seriesKey;

    private final File jpgDir;

    private final File modelsDir;
    private final File commitsDir;
    private final File pngInfoDir;
    private final File emptyPngDir;
    private final File notEmptyPngDir;
    private final File versionsDir;
    private final File blinksDir;

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

        this.pngInfoDir = new File(rtRepoDir, "png-info");
        FileUtil.createDirNotExists(pngInfoDir);

        emptyPngDir = new File(pngInfoDir, "empty");
        FileUtil.createDirNotExists(emptyPngDir);

        notEmptyPngDir = new File(pngInfoDir, "not-empty");
        FileUtil.createDirNotExists(notEmptyPngDir);

        versionsDir = new File(this.rtRepoDir, "versions");
        FileUtil.createDirNotExists(versionsDir);


        blinksDir = new File(this.rtRepoDir, "blinks");
        FileUtil.createDirNotExists(blinksDir);

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


    private static Log log = LogFactory.getLog(RtRepo.class);

    public InputSupplier<? extends InputStream> getPngInputSupplier(String pngShortSha) {
        return null;//todo
    }

    public boolean isEmptyPng(String fullFileName, ObjectId fullPngSha, InputSupplier<? extends InputStream> content) {
        ObjectStream is = null;
        try {

            File emptyPngFile = new File(emptyPngDir, fullPngSha.getName());
            File notEmptyPngFile = new File(notEmptyPngDir, fullPngSha.getName());

            if (emptyPngFile.exists()) {
                log.debug("emptyPngFile exists for[" + fullFileName + "]");
                return true;
            } else if (notEmptyPngFile.exists()) {
                return false;
            } else {
                boolean emptyPng = ImageUtil.isEmptyPng(fullFileName, content);

                if (emptyPng) {
                    log.debug("ImageUtil.isEmptyPng for[" + fullFileName + "]");
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

    public File getJpgDirForSize(@Nonnull JpgWidth jpgSize) {
        return new File(jpgDir, jpgSize.stringValue());
    }

    public File getJpgFileName(JpgKey jpgKey) {
        File jpgDirForSize = getJpgDirForSize(jpgKey.getWidth());
        String fingerprint = jpgKey.getFingerprint();
        TwoThirty8 twoThirty8 = TwoThirty8.getTwoThirty8(fingerprint);
        File jpgFileName = twoThirty8.getFileName(jpgDirForSize);
        return jpgFileName;
    }

    public boolean exists(JpgKey jpgKey) {
        File f = getJpgFileName(jpgKey);
        return f.exists();
    }

    public File getVersionsDir() {
        return versionsDir;
    }

    public File getVersionDir(RootTreeId rootTreeId) {
        return new File(getVersionsDir(), rootTreeId.stringValue());
    }

    public File getVersionWidthDir(RootTreeId rootTreeId, JpgWidth width) {
        File versionDir = getVersionDir(rootTreeId);
        String s = width.stringValue();
        return new File(versionDir, s);
    }

    public File getVersionWidthSliceDir(RootTreeId rootTreeId, JpgWidth width, Slice slice) {
        String sliceName = slice.getViewName() + "-" + slice.getAnglePadded();
        return new File(getVersionWidthDir(rootTreeId, width), sliceName);
    }

    public File getJpgSetFile(RootTreeId rootTreeId, JpgWidth width, Slice slice) {
        File vws = getVersionWidthSliceDir(rootTreeId, width, slice);
        return new File(vws, "jpgSet.ser");
    }

    public File getJpgJobStartedFile(RootTreeId rootTreeId, JpgWidth width, Slice slice) {
        File vws = getVersionWidthSliceDir(rootTreeId, width, slice);
        return new File(vws, "started.txt");
    }

    public File getJpgJobCompleteFile(RootTreeId rootTreeId, JpgWidth width, Slice slice) {
        File vws = getVersionWidthSliceDir(rootTreeId, width, slice);
        return new File(vws, "complete.txt");
    }

    public File getJpgCountFile(RootTreeId rootTreeId, JpgWidth width, Slice slice) {
        File vws = getVersionWidthSliceDir(rootTreeId, width, slice);
        return new File(vws, "jpgCount.txt");
    }

    public void createJpgGenCacheDir(RootTreeId rootTreeId, List<Slice> slices, JpgWidth jpgWidth) {

        File versionWidthDir = getVersionWidthDir(rootTreeId, jpgWidth);

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

        for (Slice slice : slices) {

            File file = this.getVersionWidthSliceDir(rootTreeId, jpgWidth, slice);
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

    public File getBlinkPngFile(PngShortSha shortSha) {
        return new File(blinksDir, shortSha.stringValue() + ".png");
    }

    @Override public boolean isBlinkPng(PngShortSha shortSha) {
        File blinkFile = getBlinkPngFile(shortSha);
        boolean exists = blinkFile.exists();
        return exists;
    }
}
