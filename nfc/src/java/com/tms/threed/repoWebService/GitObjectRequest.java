package com.tms.threed.repoWebService;

import com.tms.threed.repo.shared.RevisionParameter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;


/**
 *
 *
 * http://smartsoftdev.net/configurator-content/avalon/objects/1cd92595b34f8f2d814402a6282a9a0be76623a1.png
 * http://smartsoftdev.net/configurator-content/tundra/objects/3e498f384239a35def5bf7942aabb89ad32c9bbd.png
 *
 * http://localhost:8080/configurator-content/avalon/objects/1cd92595b34f8f2d814402a6282a9a0be76623a1.png
 * http://localhost:8080/configurator-content/tundra/objects/3e498f384239a35def5bf7942aabb89ad32c9bbd.png
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
public class GitObjectRequest extends SeriesBasedRepoRequest {

    private final RevisionParameter revisionParameter;

    public GitObjectRequest(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);

        String msg = "Invalid ObjectHandler URL: [" + getUri() + "].";

        String uri = getUri();
        String[] a = uri.split("/objects/");

        System.out.println("Arrays.toString(a): [" + Arrays.toString(a) + "]");
        if (a == null || a.length == 0) throw new NotFoundException(msg);

        String s = a[a.length - 1]; //1cd92595b34f8f2d814402a6282a9a0be76623a1.png


//        a = s.split("\\.");
//        if (a == null || a.length != 2)
//            throw new NotFoundException(msg + " Be sure that uri has an extension for mime type deduction. GIT objects do not know their mime/type.");

        this.revisionParameter = new RevisionParameter(s);
    }

    public RevisionParameter getRevisionParameter() {
        return revisionParameter;
    }

}
