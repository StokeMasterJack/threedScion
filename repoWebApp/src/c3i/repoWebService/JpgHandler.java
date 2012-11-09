package c3i.repoWebService;

import c3i.core.imageModel.shared.BaseImage;
import c3i.core.imageModel.shared.BaseImageType;
import c3i.core.threedModel.shared.BaseImageKey;
import c3i.repo.server.BrandRepos;
import com.google.common.io.Files;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import smartsoft.util.servlet.http.headers.CacheUtil;
import smartsoft.util.servlet.http.headers.LastModified;
import c3i.repo.server.Repos;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Random;

public class JpgHandler extends RepoHandler<JpgRequest> {

    public JpgHandler(BrandRepos brandRepos) {
        super(brandRepos);
    }

    @Override
    public void handle(JpgRequest r) {
        log.debug("Received request for [" + r.getRequest().getRequestURI() + "]");

        BaseImageKey jpgKey = r.getBaseImageKey();

        Repos repos = r.getRepos();
        File jpgFile = repos.getFileForJpg(jpgKey);

        HttpServletResponse response = r.getResponse();
        BaseImageType baseImageType = jpgKey.getProfile().getBaseImageType();
        response.setContentType(baseImageType.getMimeType());

        CacheUtil.addCacheForeverResponseHeaders(response);

        LastModified lastModified = new LastModified(jpgFile.lastModified());
        lastModified.addToResponse(response);

        response.setContentLength((int) jpgFile.length());
        response.setHeader("X-Content-Type-Options", "nosniff");


        try {
            ServletOutputStream os = response.getOutputStream();
            Files.copy(jpgFile, os);
        } catch (Exception e) {
            throw new NotFoundException("Problem streaming jpg object back to client", e);
        }

    }

    private void imageLoaderTestHelper() {
        boolean slowItDown = false;
        boolean randomFailure = false;
        if (slowItDown) {
            try {
                System.out.println("Sleeping...");
                Thread.sleep(5000);
                System.out.println("Awake!");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (randomFailure) {
            Random rng = new Random();
            int i = rng.nextInt(4);
            if (i == 0) {
                throw new NotFoundException();
            }
        }
    }


    protected static Log log = LogFactory.getLog(JpgHandler.class);


}