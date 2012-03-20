package com.tms.threed.repoWebService;

import com.google.common.io.Files;
import com.tms.threed.threedCore.imageModel.shared.ImJpg;
import com.tms.threed.threedCore.imageModel.shared.ImageStack;
import com.tms.threed.repo.server.JpgKey;
import com.tms.threed.repo.server.Repos;
import smartsoft.util.servlet.http.headers.LastModified;
import com.tms.threed.threedCore.threedModel.shared.ThreedModel;
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

        JpgKey jpgKey = new JpgKey(r.getSeriesKey(), r.getJpgWidth(), jpg.getFingerprint());

        File jpgFile = repos.getFileForJpg(jpgKey);


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

    private static Log log = LogFactory.getLog(JpgHandlerNoFingerprint.class);


}
