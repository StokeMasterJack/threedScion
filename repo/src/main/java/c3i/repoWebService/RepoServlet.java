package c3i.repoWebService;

import c3i.core.common.shared.BrandKey;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.Repos;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * [repo-base]/configurator-content/
 *      /index.html
 *      /sienna/2011/3d/models/[root tree sha].json
 *      /sienna/2011/3d/jpgs/wStd/[png-shas].json
 *      /sienna/2011/3d/pngs/[sha].json
 *      /sienna/2011/3d/blink/[png-sha].json
 *      /avalon/2011/vtc.txt
 *      /avalon/2011/exterior-2/wStd/3544/070/LH02/nofp.jpg
 *      /avalon/2011/2c05ba6f8d52e4ba85ae650756dc2d1423d9395d/exterior-2/wStd/3544/070/LH02/seriesfp.jpg
 *      /sienna/2011/objects/5605596
 *
 * This servlet serves up 5 things:
 *
 * 1.   pngs
 * 2.   jpgs
 * 3.   blink pngs
 * 4.   threed-model.json
 * 5.   Any git source object (backdoor)
 */
public class RepoServlet extends HttpServlet {


    private ThreedRepoApp app;
    private Logger log;

    private PngHandler pngHandler;
    private VtcHandler vtcHandler;
    private VtcMapHandler vtcMapHandler;
    private JpgHandler jpgHandler;
    private JpgHandlerSeriesFingerprint jpgHandlerSeriesFingerprint;
    private JpgHandlerNoFingerprint jpgHandlerNoFingerprint;
    private BlinkHandler blinkHandler;
    private ThreedModelHandler threedModelHandler;
    private ThreedModelHandlerJsonP threedModelHandlerJsonP;
    private GitObjectHandler gitObjectHandler;

    private BrandRepos brandRepos;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        log = Logger.getLogger(RepoServlet.class.getName());
        log.info("Initializing " + getClass().getSimpleName());

        try {
            app = ThreedRepoApp.getFromServletContext(getServletContext());
            Preconditions.checkNotNull(app);
            brandRepos = app.getBrandRepos();
            log.info(getClass().getSimpleName() + " initialization complete!");
        } catch (Throwable e) {
            String msg = "Problem initializing ThreedRepo: " + e;
            log.log(Level.SEVERE, msg, e);
            throw new RuntimeException(msg, e);
        }

        Repos repos = brandRepos.getRepos(BrandKey.TOYOTA);
        File repoBaseDir = repos.getRepoBaseDir();
        System.err.println("repoBaseDir[" + repoBaseDir + "]");

        pngHandler = new PngHandler(brandRepos);
        vtcHandler = new VtcHandler(brandRepos);
        vtcMapHandler = new VtcMapHandler(brandRepos);
        jpgHandler = new JpgHandler(brandRepos);
        jpgHandlerSeriesFingerprint = new JpgHandlerSeriesFingerprint(brandRepos);
        jpgHandlerNoFingerprint = new JpgHandlerNoFingerprint(brandRepos);
        blinkHandler = new BlinkHandler(brandRepos);
        threedModelHandler = new ThreedModelHandler(brandRepos);
        threedModelHandlerJsonP = new ThreedModelHandlerJsonP(brandRepos);
        gitObjectHandler = new GitObjectHandler(brandRepos);


    }


    @Override
    public void destroy() {
        log.info("Shutting down ThreedReposWebApp..");
        super.destroy();
        log.info("ThreedAdmin shutdown complete");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            String requestURI = request.getRequestURI();
            String contextPath = request.getContextPath();

            if (requestURI.equals(contextPath)) {
                try {
                    response.sendRedirect(requestURI + "/");
                    return;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (isIndexHtmlRequest(request, response)) {
                log.fine("isIndexHtmlRequest");
                InputStream is = getServletContext().getResourceAsStream("index.html");
                response.setContentType("text/html");
                ByteStreams.copy(is, response.getOutputStream());
            } else if (isVtcRequest(request)) {
                log.fine("isVtcRequest");
                vtcHandler.handle(new SeriesBasedRepoRequest(brandRepos, request, response));
            } else if (isVtcMapRequest(request)) {
                log.fine("isVtcMapRequest");
                vtcMapHandler.handle(new RepoRequest(brandRepos, request, response));
            } else if (isJpgRequestSeriesFingerprintRequest(request)) {
                log.fine("isJpgRequestSeriesFingerprintRequest");
                jpgHandlerSeriesFingerprint.handle(new JpgRequestSeriesFingerprint(brandRepos, request, response));
            } else if (isJpgRequestNoFingerprintRequest(request)) {
                log.fine("isJpgRequestNoFingerprintRequest");
                jpgHandlerNoFingerprint.handle(new JpgRequestNoFingerprint(brandRepos, request, response));
            } else if (isPngRequest(request)) {
                log.fine("isPngRequest");
                pngHandler.handle(new PngRequest(brandRepos, request, response));
            } else if (isJpgRequest(request)) {
                log.fine("isJpgRequest");
                jpgHandler.handle(new JpgRequest(brandRepos, request, response));
            } else if (isBlinkRequest(request)) {
                log.fine("isBlinkRequest");
                blinkHandler.handle(new SeriesBasedRepoRequest(brandRepos, request, response));
            } else if (isThreedModelRequest(request)) {
                log.fine("isThreedModelRequest");
                threedModelHandler.handle(new ThreedModelRequest(brandRepos, request, response));
            } else if (isThreedModelJsonpRequest(request)) {
                log.fine("isThreedModelJsonpRequest");
                threedModelHandlerJsonP.handle(new ThreedModelRequest(brandRepos, request, response));
            } else if (isObjectRequest(request)) {
                log.fine("isObjectRequest");
                gitObjectHandler.handle(new GitObjectRequest(brandRepos, request, response));
            } else {
                throw new NotFoundException("No handler for this URL: [" + request.getRequestURI() + "]");
            }


        } catch (Exception e) {
            e.printStackTrace();
            if (!response.isCommitted()) {
                log.log(Level.SEVERE, e.getMessage(), e);
                try {
                    response.sendError(404, e.getMessage());
                } catch (Exception e1) {
                    log.log(Level.SEVERE, "Exception sending error response to client", e);
                }
            } else {
                log.severe("Response committed. Could not send error response back to client for exception [" + e.toString() + "]");
                log.log(Level.SEVERE, e.getMessage(), e);
            }
        }

    }

    private boolean isVtcRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.endsWith("vtc.txt");
    }

    private boolean isVtcMapRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.endsWith("/vtcMap.txt") || uri.endsWith("/vtcMap.json") || uri.endsWith("/vtcMap.js");
    }


    private boolean isJpgRequestSeriesFingerprintRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.endsWith("seriesfp.jpg");
    }

    private boolean isJpgRequestNoFingerprintRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.endsWith("nofp.jpg");
    }

    private boolean isIndexHtmlRequest(HttpServletRequest request, HttpServletResponse response) {
        String requestURI = request.getRequestURI();


        String contextPath = request.getContextPath();
        String u1 = contextPath;
        String u2 = contextPath + "/";
        String u3 = contextPath + "/index.html";

        String uri = request.getRequestURI();


        return uri.equals(u1) || uri.equals(u2) || uri.equals(u3);
    }

    private boolean isPngRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        boolean retVal = uri.contains("/pngs/") && !uri.contains("/blink/") && !uri.contains("/objects/");
        return retVal;
    }

    private boolean isBlinkRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.endsWith(".png") && uri.contains("/blink/");
    }

    private boolean isJpgRequest(HttpServletRequest request) {
        return request.getRequestURI().contains("/jpgs/");
    }

    private boolean isThreedModelRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String callback = request.getParameter("callback");
        if (callback != null) return false;
        return (uri.endsWith(".json") || uri.endsWith(".js")) && uri.contains("/3d/models/");
    }

    private boolean isThreedModelJsonpRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String callback = request.getParameter("callback");
        if (callback == null) return false;
        return (uri.endsWith(".json") || uri.endsWith(".js")) && uri.contains("/3d/models/");
    }

    private boolean isObjectRequest(HttpServletRequest request) {
        return request.getRequestURI().contains("/objects/");
    }


}

