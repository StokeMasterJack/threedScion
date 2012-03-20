package com.tms.threed.repoWebService;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.tms.threed.repo.server.JpgKey;
import com.tms.threed.repo.server.Repos;
import com.tms.threed.threedCore.featureModel.shared.FeatureModel;
import com.tms.threed.threedCore.featureModel.shared.FixResult;
import com.tms.threed.threedCore.featureModel.shared.Fixer;
import com.tms.threed.threedCore.featureModel.shared.UnknownVarCodeException;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedCore.imageModel.shared.ImJpg;
import com.tms.threed.threedCore.imageModel.shared.ImageStack;
import com.tms.threed.threedCore.threedModel.shared.ThreedModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import smartsoft.util.servlet.http.headers.CacheUtil;
import smartsoft.util.servlet.http.headers.LastModified;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class JpgHandlerSeriesFingerprint extends RepoHandler<JpgRequestSeriesFingerprint> {


    public JpgHandlerSeriesFingerprint(Repos repos, ServletContext application) {
        super(repos, application);
    }

    @Override
    public void handle(JpgRequestSeriesFingerprint r) {

        log.debug("Received request for [" + r.getRequest().getRequestURI() + "]");

        if (!repos.isValidJpgWidth(r.getJpgWidth())) {
            throw new IllegalArgumentException("Bad JpgWidth: " + r.getJpgWidth());
        }

        ThreedModel threedModel = Repos.get().getThreedModel(r.getSeriesId());

        final FeatureModel featureModel = threedModel.getFeatureModel();

        Collection<Var> vars = new ArrayList<Var>();
        for (String varCode : r.getVarCodes()) {
            try {
                Var var = featureModel.get(varCode);
                vars.add(var);
            } catch (UnknownVarCodeException e) {
                //ignore
            }
        }

        ImmutableList<Var> picks = new ImmutableList.Builder<Var>().addAll(vars).build();

        FixResult fixResult = Fixer.fix(featureModel, picks);

        ImageStack imageStack = (ImageStack) threedModel.getImageStack(r.getSlice(), fixResult, r.getJpgWidth());


        //added support for single, full jpg that includes all zLayers built-int
        ImJpg jpg = imageStack.getFullJpg();

        JpgKey jpgKey = new JpgKey(r.getSeriesId().getSeriesKey(), r.getJpgWidth(), jpg.getFingerprint());

        File jpgFile = repos.getFileForJpg(jpgKey);


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


    private static Log log = LogFactory.getLog(JpgHandlerSeriesFingerprint.class);


}
