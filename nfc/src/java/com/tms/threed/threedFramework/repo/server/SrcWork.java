package com.tms.threed.threedFramework.repo.server;

import com.google.common.io.Files;
import com.tms.threed.threedFramework.threedCore.shared.SeriesKey;

import java.io.File;
import java.io.IOException;

public class SrcWork {

    private final SeriesKey seriesKey;
    private final File srcWorkDir;
    private final File modelXmlFile;

    public SrcWork(File srcWorkDir, SeriesKey seriesKey) {

        assert srcWorkDir != null;
        assert seriesKey != null;

        this.seriesKey = seriesKey;

        this.srcWorkDir = srcWorkDir;

        RepoUtil.createDirNotExists(srcWorkDir);

        modelXmlFile = new File(srcWorkDir, "model.xml");

    }

    public File getModelXmlFile() {
        return modelXmlFile;
    }

    public File getSrcWorkDir() {
        return srcWorkDir;
    }


    public void deleteContents() {
        try {
            Files.deleteDirectoryContents(srcWorkDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
