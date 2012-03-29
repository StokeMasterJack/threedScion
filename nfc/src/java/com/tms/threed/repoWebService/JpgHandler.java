package com.tms.threed.repoWebService;

import com.google.common.io.Files;
import com.tms.threed.repo.server.Repos;
import com.tms.threed.repo.shared.JpgKey;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import smartsoft.util.servlet.http.headers.CacheUtil;
import smartsoft.util.servlet.http.headers.LastModified;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Random;

public class JpgHandler extends RepoHandler<JpgRequest> {

    public JpgHandler(Repos repos, ServletContext application) {
        super(repos, application);
    }

    @Override
    public void handle(JpgRequest r) {
        log.debug("Received request for [" + r.getRequest().getRequestURI() + "]");

        imageLoaderTestHelper();

        JpgKey jpgKey = r.getJpgKey();

        File jpgFile = repos.getFileForJpg(jpgKey);

        HttpServletResponse response = r.getResponse();
        response.setContentType("image/jpeg");

        CacheUtil.addCacheForeverResponseHeaders(response);

        LastModified lastModified = new LastModified(jpgFile.lastModified());
        lastModified.addToResponse(response);

        response.setContentLength((int) jpgFile.length());
        response.setHeader("X-Content-Type-Options", "nosniff");


        try

        {
            ServletOutputStream os = response.getOutputStream();
            Files.copy(jpgFile, os);
        } catch (
                Exception e
                )

        {
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
