package c3i.repoWebService;

import c3i.core.common.shared.BrandKey;
import c3i.core.threedModel.shared.Brand;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.Repos;
import org.codehaus.jackson.node.ObjectNode;
import smartsoft.util.servlet.http.headers.CacheUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.logging.Logger;

/**
 * <repo-base>/configurator-content/avalon/2011/vtc.txt
 *
 * Redundant with gwt-rpc call: ThreedAdminService2.getVtcRootTreeId(..)
 */
public class VtcMapHandler extends RepoHandler<RepoRequest> {


    public VtcMapHandler(BrandRepos brandRepos) {
        super(brandRepos);
    }

    @Override
    public void handle(RepoRequest r) {

        HttpServletResponse response = r.getResponse();

        String callback = r.getRequest().getParameter("callback");
        String contentType;
        if (callback == null) {
            contentType = "application/json";
        } else {
            contentType = "text/javascript";
        }


        response.setContentType(contentType);
        response.setCharacterEncoding("UTF-8");

        int maxAgeBrowser = 4; //hours
        int maxAgeCdn = 24; //hours
        CacheUtil.addCacheForXHoursResponseHeaders(response, maxAgeCdn, maxAgeBrowser);

        BrandKey brandKey = r.getBrandKey();
        Repos repos = r.getRepos();
        Brand brandInitData = repos.getBrandInitData();

        BrandSerializer brandSerializer = new BrandSerializer();


        ObjectNode jsBrandInitData = brandSerializer.toJson(brandInitData);
        String jsonString = jsBrandInitData.toString();

        if (callback != null) {
            jsonString = callback + "(" + jsonString + ");";
        }

        log.fine("About to server brandInit: ");
        log.fine(jsonString);

        try {
            PrintWriter out = response.getWriter();
            out.println(jsonString);
        } catch (Exception e) {
            throw new NotFoundException("Problem streaming commitId back to client", e);
        }
    }


    private static Logger log = Logger.getLogger(VtcMapHandler.class.toString());


}
