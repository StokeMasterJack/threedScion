package c3i.repoWebService;

import c3i.core.featureModel.shared.FixedPicks;
import c3i.imageModel.shared.CoreImageStack;
import c3i.imageModel.shared.ImView;
import c3i.imageModel.shared.ImageMode;
import c3i.imageModel.shared.Profile;
import c3i.imageModel.shared.RawImageStack;
import c3i.imageModel.shared.BaseImageKey;
import c3i.imageModel.shared.Slice;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.Repos;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import java.util.logging.Logger;

import smartsoft.util.servlet.http.headers.LastModified;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

import static c3i.core.threedModel.shared.ImFeatureModel.toSimplePicks;

public class JpgHandlerNoFingerprint extends RepoHandler<JpgRequestNoFingerprint> {

    public JpgHandlerNoFingerprint(BrandRepos repos) {
        super(repos);
    }

    @Override
    public void handle(JpgRequestNoFingerprint r) {

        log.fine("Received request for [" + r.getRequest().getRequestURI() + "]");

        Repos repos = r.getRepos();
        ThreedModel threedModel = repos.getVtcThreedModel(r.getSeriesKey());

        Slice slice = r.getSlice();
        ImmutableSet<String> rawPicks = r.getVarCodes();
        Profile profile = r.getProfile();

        ImView view = threedModel.getView(slice.getViewName());
        FixedPicks fixedPicks = threedModel.fixupRaw(rawPicks);
        RawImageStack rawImageStack = view.getRawImageStack(toSimplePicks(fixedPicks), slice.getAngle());

        CoreImageStack coreImageStack = rawImageStack.getCoreImageStack(profile, ImageMode.JPG);

        BaseImageKey jpgKey = coreImageStack.getBaseImageKey();

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

    private static Logger log = Logger.getLogger("c3i");


}
