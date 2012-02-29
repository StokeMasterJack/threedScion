package com.tms.threed.threedFramework.repo.web;

import com.google.common.io.Files;
import com.tms.threed.threedFramework.imageModel.shared.ImJpg;
import com.tms.threed.threedFramework.imageModel.shared.ImageStack;
import com.tms.threed.threedFramework.jpgGen.server.singleJpg.JpgGeneratorPureJava;
import com.tms.threed.threedFramework.jpgGen.shared.Stats;
import com.tms.threed.threedFramework.repo.server.JpgId;
import com.tms.threed.threedFramework.repo.server.Repos;
import com.tms.threed.threedFramework.util.servlet.http.headers.LastModified;
import com.tms.threed.threedFramework.threedModel.shared.ThreedModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

public class JpgHandlerNoFingerprint extends RepoHandler<JpgRequestNoFingerprint> {


    public JpgHandlerNoFingerprint(Repos repos, ServletContext application) {
        super(repos, application);
    }

    @Override
    public void handle(JpgRequestNoFingerprint r) {

        log.debug("Received request for [" + r.getRequest().getRequestURI() + "]");

        if (!repos.isValidJpgWidth(r.getJpgWidth())) {
            throw new IllegalArgumentException("Bad JpgWidth: " + r.getJpgWidth());
        }

        ThreedModel threedModel = Repos.get().getVtcThreedModel(r.getSeriesKey());

        ImageStack imageStack = (ImageStack) threedModel.getImageStack(r.getSlice(), r.getVarCodes(), r.getJpgWidth());

        ImJpg jpg = imageStack.getFullJpg();

        JpgId jpgId = new JpgId(r.getSeriesKey(), r.getJpgWidth(), jpg.getFingerprint());

        File jpgFile = repos.getFileForJpg(jpgId);

        if (!jpgFile.exists()) {
            System.err.println("Creating fallback jpg on the fly: " + jpgFile);
            createJpgOnTheFly(jpgId);

            if (!jpgFile.exists()) {
                throw new RuntimeException("Failed to create fallback jpg[" + jpgFile + "]");
            }
        }


        HttpServletResponse response = r.getResponse();
        response.setContentType("image/jpeg");

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

    private void createJpgOnTheFly(JpgId jpgId) {
        Stats stats = new Stats();
        JpgGeneratorPureJava jpgGeneratorPureJava2 = new JpgGeneratorPureJava(repos, jpgId);
        jpgGeneratorPureJava2.generate(stats);
    }

    private static Log log = LogFactory.getLog(JpgHandlerNoFingerprint.class);


}
