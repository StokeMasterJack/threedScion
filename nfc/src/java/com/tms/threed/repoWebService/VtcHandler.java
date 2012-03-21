package com.tms.threed.repoWebService;

import com.tms.threed.repoService.server.Repos;
import com.tms.threed.threedCore.threedModel.shared.RootTreeId;
import com.tms.threed.threedCore.threedModel.shared.SeriesKey;
import smartsoft.util.servlet.http.headers.CacheUtil;
import smartsoft.util.servlet.http.headers.LastModified;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.PrintWriter;

/**
 * <repo-base>/configurator-content/avalon/2011/vtc.txt
 *
 * Redundant with gwt-rpc call: ThreedAdminService2.getVtcRootTreeId(..)
 */
public class VtcHandler extends RepoHandler<SeriesBasedRepoRequest> {

    public VtcHandler(Repos repos, ServletContext application) {
        super(repos, application);
    }

    @Override
    public void handle(SeriesBasedRepoRequest r) {
        SeriesKey seriesKey = r.getSeriesKey();

        RootTreeId vtcRootTreeId = repos.getVtcRootTreeId(seriesKey);

        HttpServletResponse response = r.getResponse();
        response.setContentType("text/plain");

        CacheUtil.addCacheNeverResponseHeaders(response);

        File vtcFile = repos.getVtcFile(seriesKey);
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
