package c3i.repoWebService;

import c3i.featureModel.shared.common.SeriesKey;
import c3i.imageModel.shared.PngSegment;
import c3i.ip.ZPngGenerator;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.BrandRepo;
import c3i.repo.server.SeriesRepo;
import c3i.repo.server.rt.RtRepo;
import com.google.common.io.Files;
import smartsoft.util.Date;
import smartsoft.util.servlet.http.headers.CacheUtil;
import smartsoft.util.servlet.http.headers.LastModified;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * <repo-url-base>/<repo-name>/3d/pngs/<short-sha>.png
 *
 *      http://smartsoftdev.net/configurator-content/avalon/3d/pngs/1cd92595b3.png
 */
public class PngHandler extends RepoHandler<PngRequest> {

    public static final long LAST_MODIFIED_DATE = new Date(2010, 1, 1).toLong();

    public PngHandler(BrandRepos brandRepos) {
        super(brandRepos);
    }

    @Override
    public void handle(PngRequest repoRequest) {

        log.fine("Received request for [" + repoRequest.getRequest().getRequestURI() + "]");

        SeriesKey seriesKey = repoRequest.getSeriesKey();
        PngSegment pngKey = repoRequest.getPngKey();

        File zPngFile = getFileForZPng(seriesKey, -1, pngKey, repoRequest);

        HttpServletResponse response = repoRequest.getResponse();
        response.setContentType("image/png");


        CacheUtil.addCacheForeverResponseHeaders(response);


        final long fileSize = zPngFile.length();
        response.setContentLength((int) fileSize);
        response.setHeader("X-Content-Type-Options", "nosniff");

        LastModified lastModified = new LastModified(zPngFile.lastModified());
        lastModified.addToResponse(response);

        try {
            ServletOutputStream os = response.getOutputStream();
            Files.copy(zPngFile, os);
        } catch (IOException e) {
            throw new NotFoundException("Problem streaming png object to client", e);
        }

    }

    private File getFileForZPng(SeriesKey sk, int width, PngSegment pngKey, PngRequest repoRequest) {
        BrandRepo brandRepo = repoRequest.getRepos();
        SeriesRepo seriesRepo = brandRepo.getSeriesRepo(sk);
        RtRepo genRepo = seriesRepo.getRtRepo();

        File pngFile = genRepo.getZPngFileName(pngKey);

        if (!pngFile.exists()) {
            log.warning("Creating fallback zPng on the fly: " + pngFile);
            createZPngOnTheFly(width, sk, pngKey, repoRequest);

            if (!pngFile.exists()) {
                throw new RuntimeException("Failed to create fallback zPng[" + pngFile + "]");
            }
        }

        return pngFile;
    }

    private void createZPngOnTheFly(int width, SeriesKey sk, PngSegment pngKey, PngRequest repoRequest) {
        BrandRepo brandRepo = repoRequest.getRepos();
        ZPngGenerator zPngGenerator = new ZPngGenerator(brandRepo, width, sk, pngKey);
        zPngGenerator.generate();
    }


    protected static Logger log = Logger.getLogger(PngHandler.class.getName());


}
