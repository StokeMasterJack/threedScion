package com.tms.threed.repoWebService;

import com.google.common.io.ByteStreams;
import com.tms.threed.repo.server.Repos;
import com.tms.threed.repo.server.ThreedConfig;
import smartsoft.util.config.ConfigHelper;
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

    private static final Log log;

    static {
        ConfigHelper.maybeInitLogger(RepoServlet.class.getSimpleName());
        log = LogFactory.getLog(RepoServlet.class);
    }

    private Repos repos;

    private PngHandler pngHandler;
    private VtcHandler vtcHandler;
    private JpgHandler jpgHandler;
    private JpgHandlerSeriesFingerprint jpgHandlerSeriesFingerprint;
    private JpgHandlerNoFingerprint jpgHandlerNoFingerprint;
    private BlinkHandler blinkHandler;
    private ThreedModelHandler threedModelHandler;
    private GitObjectHandler gitObjectHandler;

    private ServletContext application;


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        application = config.getServletContext();


        try {
            File repoBaseDir = ThreedConfig.getRepoBaseDir();
            Repos.setRepoBaseDir(repoBaseDir);

            this.repos = Repos.get();
            log.info(getClass().getSimpleName() + " initialization complete!");
        } catch (Throwable e) {
            String msg = "Problem initializing ThreedRepo: " + e;
            log.error(msg, e);
        }

        pngHandler = new PngHandler(repos, application);
        vtcHandler = new VtcHandler(repos, application);
        jpgHandler = new JpgHandler(repos, application);
        jpgHandlerSeriesFingerprint = new JpgHandlerSeriesFingerprint(repos, application);
        jpgHandlerNoFingerprint = new JpgHandlerNoFingerprint(repos, application);
        blinkHandler = new BlinkHandler(repos, application);
        threedModelHandler = new ThreedModelHandler(repos, application);
        gitObjectHandler = new GitObjectHandler(repos, application);


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

            if (isIndexHtmlRequest(request)) {
                log.debug("isIndexHtmlRequest");
                InputStream is = application.getResourceAsStream("index.html");
                response.setContentType("text/html");
                ByteStreams.copy(is, response.getOutputStream());
            } else if (isVtcRequest(request)) {
                log.debug("isVtcRequest");
                vtcHandler.handle(new SeriesBasedRepoRequest(request, response));
            } else if (isJpgRequestSeriesFingerprintRequest(request)) {
                log.debug("isJpgRequestSeriesFingerprintRequest");
                jpgHandlerSeriesFingerprint.handle(new JpgRequestSeriesFingerprint(request, response));
            } else if (isJpgRequestNoFingerprintRequest(request)) {
                log.debug("isJpgRequestNoFingerprintRequest");
                jpgHandlerNoFingerprint.handle(new JpgRequestNoFingerprint(request, response));
            } else if (isPngRequest(request)) {
                log.debug("isPngRequest");
                pngHandler.handle(new PngRequest(request, response));
            } else if (isJpgRequest(request)) {
                log.debug("isJpgRequest");
                jpgHandler.handle(new JpgRequest(request, response));
            } else if (isBlinkRequest(request)) {
                log.debug("isBlinkRequest");
                blinkHandler.handle(new SeriesBasedRepoRequest(request, response));
            } else if (isThreedModelRequest(request)) {
                log.debug("isThreedModelRequest");
                threedModelHandler.handle(new ThreedModelRequest(request, response));
            } else if (isObjectRequest(request)) {
                log.debug("isObjectRequest");
                gitObjectHandler.handle(new GitObjectRequest(request, response));
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


    private boolean isJpgRequestSeriesFingerprintRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.endsWith("seriesfp.jpg");
    }

    private boolean isJpgRequestNoFingerprintRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.endsWith("nofp.jpg");
    }

    private boolean isIndexHtmlRequest(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String u1 = contextPath;
        String u2 = contextPath + "/";
        String u3 = contextPath + "/index.html";

        String uri = request.getRequestURI();
        return uri.equals(u1) || uri.equals(u2) || uri.equals(u3);
    }

    private boolean isPngRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        boolean retVal = uri.endsWith(".png") && !uri.contains("/blink/") && !uri.contains("/objects/");
        return retVal;
    }

    private boolean isBlinkRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.endsWith(".png") && uri.contains("/blink/");
    }

    private boolean isJpgRequest(HttpServletRequest request) {
        return request.getRequestURI().endsWith(".jpg");
    }

    private boolean isThreedModelRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.endsWith(".json") && uri.contains("/3d/models/");
    }

    private boolean isObjectRequest(HttpServletRequest request) {
        return request.getRequestURI().contains("/objects/");
    }


}

