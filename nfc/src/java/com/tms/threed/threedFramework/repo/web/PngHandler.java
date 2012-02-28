package com.tms.threed.threedFramework.repo.web;

import com.tms.threed.threedFramework.repo.server.Repos;
import com.tms.threed.threedFramework.repo.server.SeriesRepo;
import com.tms.threed.threedFramework.repo.shared.RevisionParameter;
import com.tms.threed.threedFramework.util.servlet.http.headers.CacheUtil;
import com.tms.threed.threedFramework.util.servlet.http.headers.LastModified;
import com.tms.threed.threedFramework.util.lang.server.date.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.lib.ObjectLoader;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <repo-url-base>/<repo-name>/3d/pngs/<short-sha>.png
 *
 *      http://smartsoftdev.net/configurator-content/avalon/3d/pngs/1cd92595b3.png
 */
public class PngHandler extends RepoHandler<PngRequest> {

    public static final long LAST_MODIFIED_DATE = new Date(2010, 1, 1).toLong();

    public PngHandler(Repos repos, ServletContext application) {
        super(repos, application);
    }

    @Override
    public void handle(PngRequest repoRequest) {

        log.debug("Received request for [" + repoRequest.getRequest().getRequestURI() + "]");

        SeriesRepo seriesRepo = getSeriesRepo(repoRequest);
        RevisionParameter shortSha = repoRequest.getShortSha();

        ObjectLoader loader = seriesRepo.getPngByShortSha(shortSha.stringValue());

        HttpServletResponse response = repoRequest.getResponse();
        response.setContentType("image/png");


        CacheUtil.addCacheForeverResponseHeaders(response);

        LastModified lastModified = new LastModified(LAST_MODIFIED_DATE);
        lastModified.addToResponse(response);

        final long fileSize = loader.getSize();
        response.setContentLength((int) fileSize);
        response.setHeader("X-Content-Type-Options", "nosniff");


        try {
            ServletOutputStream os = response.getOutputStream();
            loader.copyTo(os);
        } catch (IOException e) {
            throw new NotFoundException("Problem streaming png object to client", e);
        }

    }

    protected static Log log = LogFactory.getLog(PngHandler.class);


}
