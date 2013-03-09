package c3i.repoWebService;

import c3i.core.threedModel.shared.ImFeatureModel;
import c3i.imageModel.shared.IBaseImageKey;
import c3i.imageModel.shared.SeriesKey;
import c3i.core.common.server.SrcPngLoader;
import c3i.imgGen.server.singleJpg.BaseImageGenerator;
import c3i.repo.server.RepoSrcPngLoader;
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

    public void test1() throws Exception {

    }

    private void createJpgOnTheFly(IBaseImageKey jpgKey, Repos repos) {

        SeriesKey seriesKey = jpgKey.getSeriesKey();

        c3i.core.common.shared.SeriesKey sk =
                new c3i.core.common.shared.SeriesKey(
                        seriesKey.getBrand(),
                        seriesKey.getYear(),
                        seriesKey.getName());

        final SeriesRepo seriesRepo = repos.getSeriesRepo(sk);

        SrcPngLoader pngLoader = new RepoSrcPngLoader(seriesRepo);

        RtRepo rtRepo = repos.getSeriesRepo(sk).getRtRepo();

        File outFile = rtRepo.getBaseImageFileName(jpgKey);
        BaseImageGenerator jpgGeneratorPureJava2 = new BaseImageGenerator(outFile, jpgKey, pngLoader);
        jpgGeneratorPureJava2.generate();
    }

    private static Logger log = Logger.getLogger(JpgGenHelper.class.getName());
}
