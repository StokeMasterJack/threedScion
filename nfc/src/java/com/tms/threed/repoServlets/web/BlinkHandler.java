package com.tms.threed.repoServlets.web;

import com.google.common.io.Files;
import com.tms.threed.threedFramework.imageModel.shared.PngShortSha;
import com.tms.threed.threedFramework.repo.server.Repos;
import com.tms.threed.threedFramework.repo.server.SeriesRepo;
import com.tms.threed.threedFramework.repo.server.rt.RtRepo;
import com.tms.threed.threedFramework.servletUtil.http.headers.CacheUtil;
import com.tms.threed.threedFramework.servletUtil.http.headers.LastModified;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

public class BlinkHandler extends RepoHandler {

    public BlinkHandler(Repos repos, ServletContext application) {
        super(repos, application);
    }

    @Override public void handle(SeriesBasedRepoRequest repoRequest) {
        System.out.println("BlinkHandler.handle");
        System.out.println("\t" + repoRequest.getSeriesKey());
        System.out.println("\t" + repoRequest.getUri());


        PngShortSha pngShortSha = getShortSha(repoRequest);

        SeriesRepo seriesRepo = repos.getSeriesRepo(repoRequest.getSeriesKey());
        RtRepo rtRepo = seriesRepo.getRtRepo();

        File blinkPngFile = rtRepo.getBlinkPngFile(pngShortSha);


        if (!blinkPngFile.exists()) {
            throw new RuntimeException("Failed to find blinkPngFile [" + blinkPngFile + "]");
        }


        HttpServletResponse response = repoRequest.getResponse();
        response.setContentType("image/png");

        CacheUtil.addCacheForeverResponseHeaders(response);

        LastModified lastModified = new LastModified(blinkPngFile.lastModified());
        lastModified.addToResponse(response);

        response.setContentLength((int) blinkPngFile.length());
        response.setHeader("X-Content-Type-Options", "nosniff");

        try {
            ServletOutputStream os = response.getOutputStream();
            Files.copy(blinkPngFile, os);
        } catch (Exception e) {
            throw new NotFoundException("Problem streaming jpg object back to client", e);
        }


    }

    private PngShortSha getShortSha(SeriesBasedRepoRequest repoRequest) {

        String uri = repoRequest.getUri();

        String[] a = uri.split("/");
        String pngName = a[a.length - 1];
        String[] aa = pngName.split("\\.");
        String shortSha = aa[0];

        return new PngShortSha(shortSha);


    }

}
