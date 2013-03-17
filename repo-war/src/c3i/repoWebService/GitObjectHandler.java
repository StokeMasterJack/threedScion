package c3i.repoWebService;

import c3i.repo.server.BrandRepos;
import c3i.repo.server.SeriesRepo;
import c3i.repo.server.SrcRepo;
import c3i.repo.shared.RevisionParameter;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <repo-url-base>/configurator-content/sienna/2011/objects/
 *
 *      HEAD
 *      789fd31617fac2ec2ffa482792b7fe2db63eef00
 *      789fd31617fac2ec2ffa482792b7fe2db63eef00:model.xml
 *      HEAD:model.xml
 *      HEAD:cargo/08_zAcc-DVD/DVD/LTD/Gray/vr_1_01.png
 *      5605596
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

    public GitObjectHandler(BrandRepos repos) {
        super(repos);
    }

    @Override
    public void handle(GitObjectRequest repoRequest) {
        SeriesRepo seriesRepo = repoRequest.getSeriesRepo();


        SrcRepo srcRepo = seriesRepo.getSrcRepo();

        RevisionParameter revisionParameter = repoRequest.getRevisionParameter();

        ObjectId objectId = srcRepo.resolve(revisionParameter);
        ObjectLoader loader = srcRepo.getRepoObject(objectId);

        HttpServletResponse response = repoRequest.getResponse();


        int type = loader.getType();
        String typeString = Constants.typeString(type);
        System.out.println("typeString = " + typeString);

        String contentType;
        if (type == Constants.OBJ_TREE) {
            System.out.println("OBJ_TREE");
            contentType = "text/plain";
            contentType = null;
        } else if (type == Constants.OBJ_COMMIT) {
            System.out.println("OBJ_COMMIT");
            contentType = "text/plain";
        } else if (type == Constants.OBJ_BLOB) {
            System.out.println("OBJ_BLOB");
            String extension = repoRequest.getExtension();
            if (extension != null) {
                contentType = contentTypes.get(extension);
            } else {
                contentType = null;
            }
        } else {
            contentType = null;
        }

        System.out.println("contentType = " + contentType);

        if (contentType != null) {
            response.setContentType(contentType);
        }


//        response.setHeader("Content-Encoding", "gzip");
//                response.setContentLength(retVal.length);

        //TODO: ADD CACHE FOREVER STUFF

        try {
            ServletOutputStream os = response.getOutputStream();
            loader.copyTo(os);
            os.flush();
        } catch (IOException e) {
            throw new NotFoundException("Problem streaming git object to client", e);
        }

    }


    private static Map<String, String> buildMimeMap() {
        Map<String, String> m = new HashMap<String, String>();
        m.put("png", "image/png");
        m.put("jpg", "image/jpg");
        m.put("json", "application/json");
        m.put("txt", "text/plain");
        m.put("xml", "application/xml");
        return m;
    }


}
