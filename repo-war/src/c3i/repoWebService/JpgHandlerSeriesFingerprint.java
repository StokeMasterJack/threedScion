package c3i.repoWebService;

import c3i.core.featureModel.shared.FixedPicks;
import c3i.core.imageModel.shared.BaseImage;
import c3i.core.imageModel.shared.CoreImageStack;
import c3i.core.imageModel.shared.ImView;
import c3i.core.imageModel.shared.ImageMode;
import c3i.core.imageModel.shared.Profile;
import c3i.core.imageModel.shared.RawImageStack;
import c3i.core.imageModel.shared.Slice;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.Repos;
import com.google.common.io.Files;
import java.util.logging.Logger;

import smartsoft.util.servlet.http.headers.CacheUtil;
import smartsoft.util.servlet.http.headers.LastModified;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

public class JpgHandlerSeriesFingerprint extends RepoHandler<JpgRequestSeriesFingerprint> {


    public JpgHandlerSeriesFingerprint(BrandRepos repos) {
        super(repos);
    }

    @Override
    public void handle(JpgRequestSeriesFingerprint r) {

        log.fine("Received request for [" + r.getRequest().getRequestURI() + "]");

        Repos repos = r.getRepos();
        ThreedModel threedModel = repos.getThreedModel(r.getSeriesId());

        List<String> varCodes = r.getVarCodes();
        FixedPicks fixedPicks = threedModel.fixupRaw(varCodes);

        Profile profile = r.getProfile();

        Slice slice = r.getSlice();
        String viewName = slice.getViewName();
        int angle = slice.getAngle();

        ImView view = threedModel.getView(viewName);
        RawImageStack rawImageStack = view.getRawImageStack(fixedPicks, slice.getAngle());

        CoreImageStack coreImageStack = rawImageStack.getCoreImageStack(profile, ImageMode.JPG);

        BaseImage jpgKey = coreImageStack.getBaseImage();


        //added support for single, full jpg that includes all zLayers built-int
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


    private static Logger log = Logger.getLogger("c3i");


}
