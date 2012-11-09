package c3i.repoWebService;

import c3i.core.common.shared.BrandKey;
import c3i.core.featureModel.shared.FixedPicks;
import c3i.core.imageModel.shared.CoreImageStack;
import c3i.core.imageModel.shared.ImView;
import c3i.core.imageModel.shared.ImageMode;
import c3i.core.imageModel.shared.Profile;
import c3i.core.imageModel.shared.RawImageStack;
import c3i.core.threedModel.shared.BaseImageKey;
import c3i.core.threedModel.shared.Slice;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.Repos;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import smartsoft.util.servlet.http.headers.LastModified;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

public class JpgHandlerNoFingerprint extends RepoHandler<JpgRequestNoFingerprint> {

    public JpgHandlerNoFingerprint(BrandRepos repos) {
        super(repos);
    }

    @Override
    public void handle(JpgRequestNoFingerprint r) {

        log.debug("Received request for [" + r.getRequest().getRequestURI() + "]");

        Repos repos = r.getRepos();
        ThreedModel threedModel = repos.getVtcThreedModel(r.getSeriesKey());

        Slice slice = r.getSlice();
        ImmutableSet<String> rawPicks = r.getVarCodes();
        Profile profile = r.getProfile();

        ImView view = threedModel.getView(slice.getViewName());
        FixedPicks fixedPicks = threedModel.fixupRaw(rawPicks);
        RawImageStack rawImageStack = view.getRawImageStack(fixedPicks, slice.getAngle());

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

    private static Log log = LogFactory.getLog(JpgHandlerNoFingerprint.class);


}
