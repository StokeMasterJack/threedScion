package com.tms.threed.repoServlets.web;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.tms.threed.threedFramework.featureModel.shared.FeatureModel;
import com.tms.threed.threedFramework.featureModel.shared.FixResult;
import com.tms.threed.threedFramework.featureModel.shared.Fixer;
import com.tms.threed.threedFramework.featureModel.shared.UnknownVarCodeException;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedFramework.imageModel.shared.ImJpg;
import com.tms.threed.threedFramework.imageModel.shared.ImageStack;
import com.tms.threed.threedFramework.jpgGen.server.singleJpg.JpgGeneratorPureJava;
import com.tms.threed.threedFramework.jpgGen.shared.Stats;
import com.tms.threed.threedFramework.repo.server.JpgId;
import com.tms.threed.threedFramework.repo.server.RepoHttp;
import com.tms.threed.threedFramework.repo.server.Repos;
import com.tms.threed.threedFramework.servletUtil.http.headers.CacheUtil;
import com.tms.threed.threedFramework.servletUtil.http.headers.LastModified;
import com.tms.threed.threedFramework.threedModel.shared.ThreedModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

        if (!repos.isValidJpgWidth(r.getJpgWidth())) {
            throw new IllegalArgumentException("Bad JpgWidth: " + r.getJpgWidth());
        }

        ThreedModel threedModel = RepoHttp.getThreedModel(application, r.getSeriesId());

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


        ImJpg jpg = imageStack.getJpg();

        JpgId jpgId = new JpgId(r.getSeriesId().getSeriesKey(), r.getJpgWidth(), jpg.getFingerprint());

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

    private void createJpgOnTheFly(JpgId jpgId) {
        Stats stats = new Stats();
        JpgGeneratorPureJava jpgGeneratorPureJava2 = new JpgGeneratorPureJava(repos, jpgId);
        jpgGeneratorPureJava2.generate(stats);
    }

    private static Log log = LogFactory.getLog(JpgHandlerSeriesFingerprint.class);


}
