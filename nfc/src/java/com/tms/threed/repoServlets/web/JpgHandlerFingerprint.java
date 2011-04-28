package com.tms.threed.repoServlets.web;

import com.google.common.io.Files;
import com.tms.threed.threedFramework.jpgGen.server.singleJpg.JpgGeneratorPureJava;
import com.tms.threed.threedFramework.jpgGen.shared.Stats;
import com.tms.threed.threedFramework.repo.server.Repos;
import com.tms.threed.threedFramework.repo.server.JpgId;
import com.tms.threed.threedFramework.repo.shared.JpgWidth;
import com.tms.threed.threedFramework.servletUtil.http.headers.CacheUtil;
import com.tms.threed.threedFramework.servletUtil.http.headers.LastModified;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

public class JpgHandlerFingerprint extends RepoHandler<JpgRequestFingerprint> {

    public JpgHandlerFingerprint(Repos repos, ServletContext application) {
        super(repos, application);
    }

    @Override
    public void handle(JpgRequestFingerprint r) {

        JpgId jpgId = r.getJpgId();

        JpgWidth width = jpgId.getWidth();

        if(!repos.isValidJpgWidth(width)) {
            throw new IllegalArgumentException("Bad JpgWidth: " + width);
        }

        File jpgFile = repos.getFileForJpg(jpgId);


        if (!jpgFile.exists()) {
            System.err.println("Creating fallback jpg on the fly: " + jpgFile);
            createJpgOnTheFly(r);

            if (!jpgFile.exists()) {
                throw new RuntimeException("Failed to create fallback jpg[" + jpgFile + "]");
            }
        }


        HttpServletResponse response = r.getResponse();
        response.setContentType("image/jpeg");

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

    private void createJpgOnTheFly(JpgRequestFingerprint r) {
        JpgId jpgId = r.getJpgId();
        createJpgOnTheFly(jpgId);
    }

    private void createJpgOnTheFly(JpgId jpgId) {
        Stats stats = new Stats();
        JpgGeneratorPureJava jpgGeneratorPureJava2 = new JpgGeneratorPureJava(repos, jpgId);
        jpgGeneratorPureJava2.generate(stats);
    }




}
