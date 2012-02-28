package com.tms.threed.threedFramework.repo.web;

import com.tms.threed.threedFramework.repo.server.VtcService;
import com.tms.threed.threedFramework.repo.server.Repos;
import com.tms.threed.threedFramework.repo.shared.RootTreeId;
import com.tms.threed.threedFramework.util.servlet.http.headers.CacheUtil;
import com.tms.threed.threedFramework.util.servlet.http.headers.LastModified;
import com.tms.threed.threedFramework.threedModel.shared.SeriesKey;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.PrintWriter;

public class VtcHandler extends RepoHandler<SeriesBasedRepoRequest> {

    public VtcHandler(Repos repos, ServletContext application) {
        super(repos, application);
    }

    @Override public void handle(SeriesBasedRepoRequest r) {
        SeriesKey seriesKey = r.getSeriesKey();
        VtcService vtcService = new VtcService(repos);
        RootTreeId vtcRootTreeId = vtcService.getVtcRootTreeId(seriesKey);

        HttpServletResponse response = r.getResponse();
        response.setContentType("text/plain");

        CacheUtil.addCacheNeverResponseHeaders(response);

        File vtcFile = vtcService.getVtcFile(seriesKey);
        LastModified lastModified = new LastModified(vtcFile.lastModified());
        lastModified.addToResponse(response);

        response.setContentLength(vtcRootTreeId.getName().length());
        response.setHeader("X-Content-Type-Options", "nosniff");

        try {
            PrintWriter out = response.getWriter();
            out.print(vtcRootTreeId.getName());
        } catch (Exception e) {
            throw new NotFoundException("Problem streaming commitId back to client", e);
        }
    }


}
