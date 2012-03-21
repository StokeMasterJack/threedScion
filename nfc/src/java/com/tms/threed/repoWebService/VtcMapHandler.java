package com.tms.threed.repoWebService;

import com.tms.threed.repoService.server.Repos;
import com.tms.threed.threedCore.threedModel.shared.VtcMap;
import smartsoft.util.servlet.http.headers.CacheUtil;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * <repo-base>/configurator-content/avalon/2011/vtc.txt
 *
 * Redundant with gwt-rpc call: ThreedAdminService2.getVtcRootTreeId(..)
 */
public class VtcMapHandler extends RepoHandler<RepoRequest> {


    public VtcMapHandler(Repos repos, ServletContext application) {
        super(repos, application);
    }

    @Override
    public void handle(RepoRequest r) {

        HttpServletResponse response = r.getResponse();
        response.setContentType("text/plain");

        CacheUtil.addCacheNeverResponseHeaders(response);

        VtcMap vtcMap = repos.getVtcMap();

        try {
            PrintWriter out = response.getWriter();
            out.println(vtcMap.serialize());
        } catch (Exception e) {
            throw new NotFoundException("Problem streaming commitId back to client", e);
        }
    }


}
