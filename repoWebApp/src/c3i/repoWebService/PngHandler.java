package c3i.repoWebService;

import c3i.core.common.shared.SeriesKey;
import c3i.core.imageModel.shared.PngSegment;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.Repos;
import com.google.common.io.Files;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import smartsoft.util.date.Date;
import smartsoft.util.servlet.http.headers.CacheUtil;
import smartsoft.util.servlet.http.headers.LastModified;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

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

        log.debug("Received request for [" + repoRequest.getRequest().getRequestURI() + "]");

        SeriesKey seriesKey = repoRequest.getSeriesKey();
        PngSegment pngKey = repoRequest.getPngKey();
        Repos repos = repoRequest.getRepos();
        File zPngFile = repos.getFileForZPng(seriesKey,-1,pngKey);

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

    protected static Log log = LogFactory.getLog(PngHandler.class);


}
