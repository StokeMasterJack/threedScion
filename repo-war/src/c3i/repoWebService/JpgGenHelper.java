package c3i.repoWebService;

import c3i.core.common.shared.SeriesKey;
import c3i.imageModel.shared.IBaseImageKey;
import c3i.imageModel.shared.ImContextKey;
import c3i.imgGen.api.SrcPngLoader;
import c3i.imgGen.server.singleJpg.BaseImageGenerator;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.SeriesRepo;
import c3i.repo.server.rt.RtRepo;

import java.io.File;
import java.util.logging.Logger;

public class JpgGenHelper {

    private final SrcPngLoader pngLoader;
    private final BrandRepos brandRepos;

    public JpgGenHelper(SrcPngLoader pngLoader, BrandRepos brandRepos) {
        this.pngLoader = pngLoader;
        this.brandRepos = brandRepos;
    }

    public File getFileForJpg(IBaseImageKey jpgKey) {
        ImContextKey imageModelKey = jpgKey.getSeriesKey();

        SeriesKey seriesKey = new SeriesKey(imageModelKey);

        SeriesRepo seriesRepo = brandRepos.getSeriesRepo(seriesKey);
        RtRepo rtRepo = seriesRepo.getRtRepo();

        File jpgFile = rtRepo.getBaseImageFileName(jpgKey);

        if (!jpgFile.exists()) {
            log.warning("Creating fallback jpg on the fly: " + jpgFile);
            createJpgOnTheFly(jpgKey, rtRepo);
            if (!jpgFile.exists()) {
                throw new RuntimeException("Failed to create fallback jpg[" + jpgFile + "]");
            }
        }

        return jpgFile;
    }


    private void createJpgOnTheFly(IBaseImageKey jpgKey, RtRepo rtRepo) {
        File outFile = rtRepo.getBaseImageFileName(jpgKey);

        BaseImageGenerator jpgGeneratorPureJava2 = new BaseImageGenerator(outFile, jpgKey, pngLoader);
        jpgGeneratorPureJava2.generate();
    }

    private static Logger log = Logger.getLogger(JpgGenHelper.class.getName());
}
