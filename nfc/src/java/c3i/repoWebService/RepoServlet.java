package c3i.repoWebService;

import c3i.core.common.shared.BrandKey;
import c3i.repo.server.Repos;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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
    private Log log;

    private PngHandler pngHandler;
    private VtcHandler vtcHandler;
    private BrandInitHandler brandInitHandler;
    private JpgHandler jpgHandler;
    private JpgHandlerSeriesFingerprint jpgHandlerSeriesFingerprint;
    private JpgHandlerNoFingerprint jpgHandlerNoFingerprint;
    private BlinkHandler blinkHandler;
    private ThreedModelHandler threedModelHandler;
    private ThreedModelHandlerJsonP threedModelHandlerJsonP;
    private GitObjectHandler gitObjectHandler;

    private ServletContext application;

    private Repos repos;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        application = config.getServletContext();

        app = new ThreedRepoApp();
        Preconditions.checkNotNull(app);
        log = LogFactory.getLog(RepoServlet.class);

        try {
            repos = initRepos(app.getRepoBaseDirs());
            log.info(getClass().getSimpleName() + " initialization complete!");
        } catch (Throwable e) {
            String msg = "Problem initializing ThreedRepo: " + e;
            log.error(msg, e);
            throw new RuntimeException(msg,e);
        }

        pngHandler = new PngHandler(repos, application);
        vtcHandler = new VtcHandler(repos, application);
        brandInitHandler = new BrandInitHandler(repos, application);
        jpgHandler = new JpgHandler(repos, application);
        jpgHandlerSeriesFingerprint = new JpgHandlerSeriesFingerprint(repos, application);
        jpgHandlerNoFingerprint = new JpgHandlerNoFingerprint(repos, application);
        blinkHandler = new BlinkHandler(repos, application);
        threedModelHandler = new ThreedModelHandler(repos, application);
        threedModelHandlerJsonP = new ThreedModelHandlerJsonP(repos, application);
        gitObjectHandler = new GitObjectHandler(repos, application);


    }

    private static Repos initRepos(Map<String, String> map) {
        Map<BrandKey, File> repoBaseDirMap = new HashMap<BrandKey, File>();
        for (String brand : map.keySet()) {
            BrandKey brandKey = BrandKey.fromString(brand);
            String sRepoBaseDir = map.get(brand);
            File repoBaseDir = new File(sRepoBaseDir);
            repoBaseDirMap.put(brandKey, repoBaseDir);
        }
        return new Repos(repoBaseDirMap);
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
                log.debug("isIndexHtmlRequest");
                InputStream is = application.getResourceAsStream("index.html");
                response.setContentType("text/html");
                ByteStreams.copy(is, response.getOutputStream());
            } else if (isVtcRequest(request)) {
                log.debug("isVtcRequest");
                vtcHandler.handle(new SeriesBasedRepoRequest(repos,request, response));
            } else if (isBrandInitRequest(request)) {
                log.debug("isBrandInitRequest");
                brandInitHandler.handle(new RepoRequest(repos,request, response));
            } else if (isJpgRequestSeriesFingerprintRequest(request)) {
                log.debug("isJpgRequestSeriesFingerprintRequest");
                jpgHandlerSeriesFingerprint.handle(new JpgRequestSeriesFingerprint(repos,request, response));
            } else if (isJpgRequestNoFingerprintRequest(request)) {
                log.debug("isJpgRequestNoFingerprintRequest");
                jpgHandlerNoFingerprint.handle(new JpgRequestNoFingerprint(repos,request, response));
            } else if (isPngRequest(request)) {
                log.debug("isPngRequest");
                pngHandler.handle(new PngRequest(repos,request, response));
            } else if (isJpgRequest(request)) {
                log.debug("isJpgRequest");
                jpgHandler.handle(new JpgRequest(repos,request, response));
            } else if (isBlinkRequest(request)) {
                log.debug("isBlinkRequest");
                blinkHandler.handle(new SeriesBasedRepoRequest(repos,request, response));
            } else if (isThreedModelRequest(request)) {
                log.debug("isThreedModelRequest");
                threedModelHandler.handle(new ThreedModelRequest(repos,request, response));
            } else if (isThreedModelJsonpRequest(request)) {
                log.debug("isThreedModelJsonpRequest");
                threedModelHandlerJsonP.handle(new ThreedModelRequest(repos,request, response));
            } else if (isObjectRequest(request)) {
                log.debug("isObjectRequest");
                gitObjectHandler.handle(new GitObjectRequest(repos,request, response));
            } else {
                throw new NotFoundException("No handler for this URL: [" + request.getRequestURI() + "]");
            }


        } catch (Exception e) {
            e.printStackTrace();
            if (!response.isCommitted()) {
                log.error(e.getMessage(), e);
                try {
                    response.sendError(404, e.getMessage());
                } catch (Exception e1) {
                    log.error("Exception sending error response to client", e);
                }
            } else {
                log.error("Response committed. Could not send error response back to client for exception [" + e.toString() + "]");
                log.error(e.getMessage(), e);
            }
        }

    }

    private boolean isVtcRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.endsWith("vtc.txt");
    }

    private boolean isBrandInitRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.endsWith("/vtcMap.txt");
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
        return uri.endsWith(".json") && uri.contains("/3d/models/");
    }

    private boolean isThreedModelJsonpRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.endsWith(".js") && uri.contains("/3d/models/");
    }

    private boolean isObjectRequest(HttpServletRequest request) {
        return request.getRequestURI().contains("/objects/");
    }


}

