package c3i.repoWebService;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import c3i.core.featureModel.shared.FixedPicks;
import c3i.core.imageModel.shared.*;
import c3i.core.threedModel.shared.*;
import c3i.repo.server.Repos;
import smartsoft.util.servlet.http.headers.LastModified;
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

        ThreedModel threedModel = Repos.get().getVtcThreedModel(r.getSeriesKey());

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
