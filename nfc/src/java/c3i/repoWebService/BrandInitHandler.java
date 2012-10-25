package c3i.repoWebService;

import c3i.core.common.shared.BrandKey;
import c3i.core.threedModel.shared.Brand;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.Repos;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.node.ObjectNode;
import smartsoft.util.servlet.http.headers.CacheUtil;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * <repo-base>/configurator-content/avalon/2011/vtc.txt
 *
 * Redundant with gwt-rpc call: ThreedAdminService2.getVtcRootTreeId(..)
 */
public class BrandInitHandler extends RepoHandler<RepoRequest> {


    public BrandInitHandler(BrandRepos brandRepos, ServletContext application) {
        super(brandRepos, application);
    }

    @Override
    public void handle(RepoRequest r) {

        HttpServletResponse response = r.getResponse();
        response.setContentType("text/plain");

        CacheUtil.addCacheNeverResponseHeaders(response);

        BrandKey brandKey = r.getBrandKey();
        Repos repos = r.getRepos();
        Brand brandInitData = repos.getBrandInitData();

        BrandSerializer brandSerializer = new BrandSerializer();


        ObjectNode jsBrandInitData = brandSerializer.toJson(brandInitData);
        String jsonString = jsBrandInitData.toString();

        log.debug("About to server brandInit: ");
        log.debug(jsonString);

        try {
            PrintWriter out = response.getWriter();
            out.println(jsonString);
        } catch (Exception e) {
            throw new NotFoundException("Problem streaming commitId back to client", e);
        }
    }


    private static Log log = LogFactory.getLog(BrandInitHandler.class);


}
