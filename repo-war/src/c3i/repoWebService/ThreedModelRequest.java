package c3i.repoWebService;

import c3i.featureModel.shared.common.RootTreeId;
import c3i.featureModel.shared.common.SeriesId;
import c3i.repo.server.BrandRepos;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * <repo-url-base>/<repo-name>/3d/models/<commit-sha>.json
 *
 * http://smartsoftdev.net/configurator-content/avalon/3d/models/d1ed8dcb174ee13018ff19ed0ced61f60666ae76.json
 * http://localhost:8080/configurator-content/avalon/3d/models/d1ed8dcb174ee13018ff19ed0ced61f60666ae76.json
 *
 */
public class ThreedModelRequest extends SeriesBasedRepoRequest {

    protected final RootTreeId rootTreeId;

    public ThreedModelRequest(BrandRepos repos,HttpServletRequest request, HttpServletResponse response) {
        super(repos,request, response);

        String msg = "Invalid ThreedModelRequest URL: [" + getUri() + "].";

        String uri = getUri();
        String[] a = uri.split("/3d/models/");
        if (a == null || a.length == 0) throw new NotFoundException(msg);

        String s = a[a.length - 1]; //d1ed8dcb174ee13018ff19ed0ced61f60666ae76.json


        int lastDot = s.lastIndexOf('.');
        if (lastDot == -1) throw new NotFoundException(msg);

//        a = s.split("\\.");
//        if (a == null || a.length != 2)
//            throw new NotFoundException(msg);

        String sRootTreeId = s.substring(0, lastDot);
        this.rootTreeId = new RootTreeId(sRootTreeId);


    }

    public RootTreeId getRootTreeId() {
        return rootTreeId;
    }

    public SeriesId getSeriesId(){
        return new SeriesId(seriesKey, getRootTreeId());
    }
}
