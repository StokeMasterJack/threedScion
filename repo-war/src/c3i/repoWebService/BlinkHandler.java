package c3i.repoWebService;

import c3i.repo.server.BrandRepos;
import com.google.common.io.Files;
import c3i.imageModel.shared.PngShortSha;
import c3i.repo.server.Repos;
import c3i.repo.server.SeriesRepo;
import c3i.repo.server.rt.RtRepo;
import smartsoft.util.servlet.http.headers.CacheUtil;
import smartsoft.util.servlet.http.headers.LastModified;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

public class BlinkHandler extends RepoHandler<SeriesBasedRepoRequest> {

    public BlinkHandler(BrandRepos repos) {
        super(repos);
    }

    @Override public void handle(SeriesBasedRepoRequest repoRequest) {
        System.out.println("BlinkHandler.handle");
        System.out.println("\t" + repoRequest.getSeriesKey());
        System.out.println("\t" + repoRequest.getUri());


        PngShortSha pngShortSha = getShortSha(repoRequest);

        Repos repos = repoRequest.getRepos();
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
