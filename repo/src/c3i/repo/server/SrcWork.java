package c3i.repo.server;

import c3i.featureModel.shared.common.SeriesKey;
import smartsoft.util.FileUtil;

import java.io.File;

public class SrcWork {

    private final SeriesKey seriesKey;
    private final File srcWorkDir;
    private final File modelXmlFile;

    public SrcWork(File srcWorkDir, SeriesKey seriesKey) {

        assert srcWorkDir != null;
        assert seriesKey != null;

        this.seriesKey = seriesKey;

        this.srcWorkDir = srcWorkDir;

        FileUtil.createDirNotExists(srcWorkDir);

        modelXmlFile = new File(srcWorkDir, "model.xml");

    }

    public File getModelXmlFile() {
        return modelXmlFile;
    }

    public File getSrcWorkDir() {
        return srcWorkDir;
    }


//    public void deleteContents() {
//        try {
//            Files.deleteDirectoryContents(srcWorkDir);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

}
