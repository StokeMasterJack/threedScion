package c3i.repoWebService;

import c3i.core.threedModel.shared.ImFeatureModel;
import c3i.imageModel.shared.IBaseImageKey;
import c3i.imgGen.server.singleJpg.BaseImageGenerator;
import c3i.repo.server.Repos;
import c3i.repo.server.SeriesRepo;
import c3i.repo.server.rt.RtRepo;

import java.io.File;
import java.util.logging.Logger;

public class JpgGenHelper {

    public File getFileForJpg(IBaseImageKey jpgKey, Repos repos) {
        c3i.imageModel.shared.SeriesKey seriesKey = jpgKey.getSeriesKey();
        SeriesRepo seriesRepo = repos.getSeriesRepo(ImFeatureModel.imToFmSeriesKey(seriesKey));
        RtRepo genRepo = seriesRepo.getRtRepo();
        File jpgFile = genRepo.getBaseImageFileName(jpgKey);

        if (!jpgFile.exists()) {
            log.warning("Creating fallback jpg on the fly: " + jpgFile);
            createJpgOnTheFly(jpgKey, repos);

            if (!jpgFile.exists()) {
                throw new RuntimeException("Failed to create fallback jpg[" + jpgFile + "]");
            }
        }

        return jpgFile;
    }

    private void createJpgOnTheFly(IBaseImageKey jpgKey, Repos repos) {
        BaseImageGenerator jpgGeneratorPureJava2 = new BaseImageGenerator(repos, jpgKey);
        jpgGeneratorPureJava2.generate();
    }

    private static Logger log = Logger.getLogger(JpgGenHelper.class.getName());
}
