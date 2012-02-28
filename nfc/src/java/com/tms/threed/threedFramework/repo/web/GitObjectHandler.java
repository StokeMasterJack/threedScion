package com.tms.threed.threedFramework.repo.web;

import com.tms.threed.threedFramework.repo.server.Repos;
import com.tms.threed.threedFramework.repo.server.SeriesRepo;
import com.tms.threed.threedFramework.repo.server.SrcRepo;
import com.tms.threed.threedFramework.repo.shared.RevisionParameter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * http://smartsoftdev.net/configurator-content/avalon/objects/1cd92595b34f8f2d814402a6282a9a0be76623a1.png
 * http://smartsoftdev.net/configurator-content/tundra/objects/3e498f384239a35def5bf7942aabb89ad32c9bbd.png
 *
 * <repo-url-base>/<repo-name>/objects/<revisionParameter>.png|jpg|json
 *
 * Turns a revisionParameter into an object
 *
 * A revisionParameter - an extended SHA1 object name.
 *
 *      jgit calls this a "git revision string" and also a "git object references expression"
 *      git calls this a "revision parameter" using an "extended SHA1"
 *
 *      For a more complete list of ways to spell object names, see SPECIFYING REVISIONS [object names] section in git-rev-parse
 *
 *      In our case it will mostly be used the short-sha of a png
 */
public class GitObjectHandler extends RepoHandler<GitObjectRequest> {

    private static Map<String, String> contentTypes = buildMimeMap();

    public GitObjectHandler(Repos repos, ServletContext application) {
        super(repos, application);
    }

    @Override public void handle(GitObjectRequest repoRequest) {
        SeriesRepo seriesRepo = getSeriesRepo(repoRequest);

        String contentType = contentTypes.get(repoRequest.getExtension());
        if (contentType == null)
            throw new NotFoundException("Unsupported extension: [" + repoRequest.getExtension() + "]");

        SrcRepo srcRepo = seriesRepo.getSrcRepo();

        RevisionParameter revisionParameter = repoRequest.getRevisionParameter();

        ObjectId objectId = srcRepo.resolve(revisionParameter);
        ObjectLoader loader = srcRepo.getRepoObject(objectId);

        HttpServletResponse response = repoRequest.getResponse();
        response.setContentType(contentType);

        //TODO: ADD CACHE FOREVER STUFF

        try {
            ServletOutputStream os = response.getOutputStream();
            loader.copyTo(os);
        } catch (IOException e) {
            throw new NotFoundException("Problem streaming git object to client", e);
        }

    }


    private static Map<String, String> buildMimeMap() {
        Map<String, String> m = new HashMap<String, String>();
        m.put("png", "image/png");
        m.put("jpg", "image/jpg");
        m.put("json", "text/json");
        return m;
    }


}
