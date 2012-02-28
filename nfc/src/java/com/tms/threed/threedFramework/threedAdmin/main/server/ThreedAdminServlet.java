package com.tms.threed.threedFramework.threedAdmin.main.server;

import com.google.common.collect.ImmutableList;
import com.google.gwt.rpc.server.ClientOracle;
import com.google.gwt.rpc.server.RpcServlet;
import com.google.gwt.user.client.rpc.SerializationException;
import com.tms.threed.threedFramework.threedAdmin.main.shared.InitData;
import com.tms.threed.threedFramework.threedAdmin.main.shared.ThreedAdminService2;
import com.tms.threed.threedFramework.jpgGen.server.JobStatus;
import com.tms.threed.threedFramework.jpgGen.server.taskManager.EquivalentJobAlreadyRunningException;
import com.tms.threed.threedFramework.jpgGen.server.taskManager.JpgGenStatusVersionWidth;
import com.tms.threed.threedFramework.jpgGen.server.taskManager.JpgGenStatusVersionWidthSlice;
import com.tms.threed.threedFramework.jpgGen.server.taskManager.JpgGeneratorService;
import com.tms.threed.threedFramework.jpgGen.server.taskManager.JpgSetAction;
import com.tms.threed.threedFramework.jpgGen.server.taskManager.Master;
import com.tms.threed.threedFramework.jpgGen.shared.ExecutorStatus;
import com.tms.threed.threedFramework.jpgGen.shared.JobId;
import com.tms.threed.threedFramework.jpgGen.shared.JobState;
import com.tms.threed.threedFramework.jpgGen.shared.Stats;
import com.tms.threed.threedFramework.repo.server.BlinkCheckin;
import com.tms.threed.threedFramework.repo.server.RepoHttp;
import com.tms.threed.threedFramework.repo.server.Repos;
import com.tms.threed.threedFramework.repo.server.SeriesRepo;
import com.tms.threed.threedFramework.repo.server.SrcRepo;
import com.tms.threed.threedFramework.repo.server.VtcService;
import com.tms.threed.threedFramework.repo.shared.CommitHistory;
import com.tms.threed.threedFramework.repo.shared.CommitId;
import com.tms.threed.threedFramework.repo.shared.JpgWidth;
import com.tms.threed.threedFramework.repo.shared.RepoHasNoHeadException;
import com.tms.threed.threedFramework.repo.shared.RootTreeId;
import com.tms.threed.threedFramework.repo.shared.RtConfig;
import com.tms.threed.threedFramework.repo.shared.SeriesNamesWithYears;
import com.tms.threed.threedFramework.threedModel.server.ThreedConfig;
import com.tms.threed.threedFramework.threedModel.shared.SeriesId;
import com.tms.threed.threedFramework.threedModel.shared.SeriesKey;
import com.tms.threed.threedFramework.threedModel.shared.*;
import com.tms.threed.threedFramework.threedModel.shared.Slice;
import com.tms.threed.threedFramework.threedModel.shared.ViewKey;
import com.tms.threed.threedFramework.util.lang.shared.Path;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.TextNode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.tms.threed.threedFramework.util.date.shared.StringUtil.isEmpty;
import static com.tms.threed.threedFramework.util.lang.server.StringUtil.notEmpty;

//import com.tms.threed.threedFramework.repo.shared.SeriesCommit;

/**
 * <repo-url-base>/seriesList.json
 * <p/>
 * http://127.0.0.1:8888/com.tms.threed.testHarness.TestHarness/series-list.json
 * http://smartsoftdev.net/configurator-content/seriesList.json
 *
 * http://127.0.0.1:8888/testHarnessGen/series-list.json?command=getJpgJobs&commitRevisionParameter=v2.0&seriesName=avalon&seriesYear=2011
 *
 * http://127.0.0.1:8888/testHarnessGen/series-list.json?command=getTags&seriesName=avalon&seriesYear=2011
 *
 * http://127.0.0.1:8888/threedAdminService.json?command=checkin

 */
public class ThreedAdminServlet extends RpcServlet implements ThreedAdminService2 {

    private final static Log log;

    static {
//        ConfigHelper.maybeInitLogger(ThreedAdminServlet.class.getSimpleName());
        log = LogFactory.getLog(ThreedAdminServlet.class);
    }

    private final JsonNodeFactory f = JsonNodeFactory.instance;
    private Repos repos;
    private JpgGeneratorService jpgGen;


    private ServletContext application;

    private String initErrorMessage;


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        log.info("Initializing " + getClass().getSimpleName());
        application = config.getServletContext();

        try {
            this.repos = RepoHttp.getRepos(application);

            jpgGen = new JpgGeneratorService(repos);
            log.info(getClass().getSimpleName() + " initialization complete!");
        } catch (Throwable e) {
            this.initErrorMessage = "Problem initializing ThreedAdminServletJson: " + e;
            log.error(initErrorMessage, e);
        }


    }

    @Override
    public void destroy() {
        super.destroy();
        log.info("Shutting down ThreedAdminWebApp..");

        log.info("\t Shutting down JpgGenerator..");
        jpgGen.stopAndWait();
        log.info("\tJpgGenerator shutdown complete");


        log.info("ThreedAdmin shutdown complete");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {

        String command = req.getParameter("command");

        if (initErrorMessage != null) {
            processErrorResponse(500, command, null, initErrorMessage, response);
        } else {

            try {
                JsonNode retVal = null;

                String pathInfo = req.getPathInfo();
                String servletPath = req.getServletPath();


                if (servletPath != null && servletPath.equals("/load_pngs")) {
                    retVal = checkinFromTeamSite(req);
                } else if (command.equals("seriesKeys")) {
                    retVal = getSeriesList();
                }


//                else if (command.equals("tags")) {
//                    retVal = getTags(req);
//                }


                else if (command.equals("startJpgJob")) {
                    retVal = startJpgJob(req);
                } else if (command.equals("views")) {
                    retVal = getViews(req);
                } else if (command.equals("jpgGenStatus")) {
                    retVal = getJpgGenStatus(req);
                } else if (command.equals("jpgQueueStatus")) {
                    retVal = getJpgQueueStatus(req);
                } else if (command.equals("load_pngs")) {
                    retVal = checkinFromTeamSite(req);
                } else if (command.equals("jpgQueueDetails")) {
                    retVal = getJpgQueueDetails(req);
                } else if (command.equals("cancelJob")) {
                    retVal = cancelJob(req);
                } else if (command.equals("removeJob")) {
                    retVal = removeJob(req);
                }


//                else if (command.equals("tagCurrentVersion")) {
//                    tagCurrentVersion(req);
//                }


                else if (command.equals("removeTerminal")) {
                    retVal = removeTerminal(req);
                } else {
                    throw new IllegalArgumentException("Bad command: " + command);
                }
                if (retVal != null) {
                    processSuccessResponse(retVal, response);
                }
            } catch (IllegalArgumentException e) {
                processErrorResponse(400, command, e, null, response);
            } catch (Throwable e) {
                processErrorResponse(500, command, e, null, response);
            }
        }

    }


    private void processErrorResponse(int statusCode, String command, Throwable e, String extraMessage, HttpServletResponse response) throws IOException {
        String msg = "Problem handling http request command[" + command + "] ";
        if (e != null) msg += " " + e.toString();
        if (extraMessage != null) msg += "  " + extraMessage;
        log.error(msg, e);

        response.setStatus(statusCode);
        TextNode retVal = f.textNode(msg);

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.write(retVal.toString());
    }

    private void processSuccessResponse(JsonNode retVal, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.write(retVal.toString());
    }

    private ArrayNode getSeriesList() {
        List<String> seriesNames = repos.getRepoNames();

        ArrayNode a = f.arrayNode();

        for (String seriesName : seriesNames) {
            ObjectNode jsSeriesObject = f.objectNode();
            jsSeriesObject.put("name", seriesName);


            ArrayNode jsYears = f.arrayNode();
            List<Integer> years = repos.getYearsForSeries(seriesName);
            for (Integer year : years) {
                jsYears.add(year);
            }

            jsSeriesObject.put("years", jsYears);

            a.add(jsSeriesObject);
        }
        return a;
    }

//
//    private ObjectNode createJsCommit(TagCommit tagCommit) {
//        assert tagCommit != null;
//
//        ObjectNode jsSeriesObject = f.objectNode();
//
//        String tagShortName = tagCommit.getShortTagName();
//        String commitId = tagCommit.getCommitId();
//
//        jsSeriesObject.put("shortTagName", tagShortName);
//        jsSeriesObject.put("commitId", commitId);
//        jsSeriesObject.put("head", tagCommit.isHead());
//
//        return jsSeriesObject;
//    }

//    private String getParentsFromSeriesCommit(SeriesCommit seriesCommit) {
//        CommitId[] parents = seriesCommit.getParents();
//        if (parents == null || parents.length == 0) return null;
//
//
//        String[] a = new String[parents.length];
//        for (int i = 0; i < a.length; i++) {
//            a[i] = parents[i].getName();
//        }
//
//
//        return Joiner.on(',').join(a);
//    }

    public JsonNode getJpgQueueStatus(HttpServletRequest request) {
        Collection<Master> masterJobs = jpgGen.getMasterJobs();


        ArrayNode jsArray = f.arrayNode();

        for (Master job : masterJobs) {

            ObjectNode jsObject = f.objectNode();


            SeriesId seriesId = job.getSeriesId();
            putSeriesId(jsObject, seriesId);


            SeriesRepo seriesRepo = repos.getSeriesRepo(seriesId.getSeriesKey());

            jsObject.put("jobId", job.getId().toString());

            jsObject.put("startTime", job.getId().getEnqueueTime());


            jsObject.put("jpgWidth", job.getJpgWidth().stringValue());


            JobStatus status = job.getStatus();

            JobState state = status.getState();
            jsObject.put("state", state.name());


            Integer jpgCount = status.getJpgCount();
            if (jpgCount != null) {
                jsObject.put("jpgCount", jpgCount);
            }

            Integer sliceCount = status.getSliceCount();
            if (sliceCount != null) {
                jsObject.put("sliceCount", sliceCount);
            }


            Integer slicesComplete = status.getSlicesComplete();
            if (slicesComplete != null) {
                jsObject.put("slicesComplete", slicesComplete);
            }

            Integer jpgsComplete = status.getJpgsComplete();
            if (jpgsComplete != null) {
                jsObject.put("jpgsComplete", jpgsComplete);
            }

            String exception = status.getException();
            if (exception != null) {
                jsObject.put("exception", exception);
            }


            jsArray.add(jsObject);
        }


        return jsArray;
    }

    public JsonNode cancelJob(HttpServletRequest request) {
        String sJobId = request.getParameter("jobId");
        JobId jobId = new JobId(sJobId);
        log.warn("Cancelling jpg job: " + jobId);
        jpgGen.cancelJob(jobId);
        return f.textNode("OK");
    }

    public JsonNode removeJob(HttpServletRequest request) {
        String sJobId = request.getParameter("jobId");
        JobId jobId = new JobId(sJobId);
        jpgGen.removeJob(jobId);
        return f.textNode("OK");
    }


    public JsonNode removeTerminal(HttpServletRequest request) {
        jpgGen.removeTerminal();
        return f.textNode("OK");
    }

    public ArrayNode getJpgQueueDetails(HttpServletRequest request) {

        String sJobId = request.getParameter("jobId");
        JobId jobId = new JobId(sJobId);
        Master masterJob = jpgGen.getJob(jobId);


        ArrayNode a = f.arrayNode();

        if (masterJob == null) {
            return a;
        }


        ImmutableList<ExecutorStatus> executorStatuses = masterJob.getExecutorStatuses();


        for (ExecutorStatus d : executorStatuses) {
            ObjectNode oo = f.objectNode();
            oo.put("name", d.getName());
            oo.put("taskCount", d.getTaskCount());
            oo.put("activeTaskCount", d.getActiveTaskCount());
            oo.put("completedTaskCount", d.getCompletedTaskCount());
            oo.put("shutdown", d.isShutdown());
            oo.put("terminated", d.isTerminated());
            a.add(oo);
        }


        return a;
    }


    public JsonNode getJpgGenStatus(HttpServletRequest request) {
        SeriesId seriesId = getSeriesId(request);
        JpgWidth jpgWidth = getJpgWidth(request);

        JpgGenStatusVersionWidth status = new JpgGenStatusVersionWidth(repos, seriesId, jpgWidth);


        List<JpgGenStatusVersionWidthSlice> slices = status.getSlices();

        System.out.println("slices = " + slices);


        ArrayNode jsArray = f.arrayNode();

        for (JpgGenStatusVersionWidthSlice slice : slices) {
            System.out.println("\t one-slice = " + slice.getSlice());
            ObjectNode jsObject = f.objectNode();
            putSlice(jsObject, slice.getSlice());

            Integer jpgCount = slice.getJpgCount();

            jsObject.put("started", slice.getStarted());
            jsObject.put("jpgCount", (Integer) jpgCount);
            jsObject.put("jpgSet", slice.jpgSetFileExists());
            jsObject.put("complete", slice.getComplete());


            jsArray.add(jsObject);
        }


        return jsArray;
    }

    public JsonNode getMissingJpgCount(HttpServletRequest request) {
        SeriesId seriesId = getSeriesId(request);
        JpgWidth jpgWidth = getJpgWidth(request);

        ThreedModel threedModel = repos.getThreedModel(seriesId);

        Slice slice = this.getSlice(request, threedModel);

        JpgSetAction jpgSetAction = new JpgSetAction(repos, seriesId, slice, jpgWidth);

        Integer count = jpgSetAction.countMissingJpgs();

        ObjectNode jsObject = f.objectNode();
        putSeriesId(jsObject, seriesId);
        putJpgWidth(jsObject, jpgWidth);
        putSlice(jsObject, slice);
        jsObject.put("missingJpgCount", count);

        return jsObject;
    }


    public ArrayNode getViews(HttpServletRequest request) {

        SeriesId seriesId = getSeriesId(request);

        ThreedModel threedModel = repos.getThreedModel(seriesId);

        ViewKey[] views = threedModel.getViewKeys();

        ArrayNode a = f.arrayNode();

        for (ViewKey view : views) {
            ObjectNode jsView = f.objectNode();
            jsView.put("view", view.getName());
            jsView.put("angleCount", view.getAngleCount());
            a.add(jsView);
        }

        return a;


    }


    public ObjectNode startJpgJob(HttpServletRequest request) {
        SeriesId seriesId = getSeriesId(request);
        JpgWidth jpgWidth = getJpgWidth(request);

        String msg;
        try {
            jpgGen.startNewJpgJob(seriesId, jpgWidth, repos.getRtConfig().getJpgGenThreadCount());
            msg = "OK";
        } catch (EquivalentJobAlreadyRunningException e) {
            msg = "AlreadyRunningException";
        }

        ObjectNode jsRetVal = f.objectNode();
        jsRetVal.put("message", msg);
        return jsRetVal;
    }


    private Slice getSlice(HttpServletRequest request, ThreedModel threedModel) {
        String view = request.getParameter("view");
        String sAngle = request.getParameter("angle");

        Integer angle = new Integer(sAngle);
        return threedModel.getSlice(view, angle);
    }

    private void putSlice(ObjectNode jsObject, Slice slice) {
        jsObject.put("view", slice.getViewName());
        jsObject.put("angle", slice.getAngle());
    }

    private JpgWidth getJpgWidth(HttpServletRequest request) {
        String jpgWidth = request.getParameter("jpgWidth");
        return new JpgWidth(jpgWidth);
    }

    private void putJpgWidth(ObjectNode jsObject, JpgWidth jpgWidth) {
        jsObject.put("jpgWidth", jpgWidth.stringValue());
    }

    private void putSeriesKey(ObjectNode jsObject, SeriesKey seriesKey) {
        jsObject.put("seriesName", seriesKey.getName());
        jsObject.put("seriesYear", seriesKey.getYear());
    }

    private void putSeriesId(ObjectNode jsObject, SeriesId seriesId) {
        putSeriesKey(jsObject, seriesId.getSeriesKey());
        jsObject.put(RootTreeId.NAME, seriesId.getRootTreeId().getName());
    }

    private SeriesKey getSeriesKey(HttpServletRequest request) {
        String seriesName = request.getParameter("seriesName");
        String seriesYear = request.getParameter("seriesYear");

        if (isEmpty(seriesName)) throw new IllegalArgumentException("seriesName is a required request parameter");
        if (isEmpty(seriesYear)) throw new IllegalArgumentException("seriesYear is a required request parameter");

        return new SeriesKey(seriesYear, seriesName);
    }

    private SeriesId getSeriesId(HttpServletRequest request) {
        SeriesKey seriesKey = getSeriesKey(request);
        String rootTreeId = request.getParameter(RootTreeId.NAME);

        if (isEmpty(rootTreeId))
            throw new IllegalArgumentException("rootTreeId is a required request parameter");
        return new SeriesId(seriesKey, new RootTreeId(rootTreeId));
    }

    @Override
    public InitData getInitData() {
        ArrayList<SeriesNamesWithYears> seriesNamesWithYears = repos.getSeriesNamesWithYears();
        RtConfig rtConfig = repos.getRtConfig();
        log.info("serving [" + rtConfig + "]");

        String repoBaseUrlName = ThreedConfig.getRepoBaseUrlName();
        if (isEmpty(repoBaseUrlName)) {
            throw new IllegalStateException("repoBaseUrlName is empty. It was pulled from config file[" + ThreedConfig.getConfigFile() + "]");
        } else {
            log.info("repoBaseUrlName is [" + repoBaseUrlName + ". It was pulled from config file[" + ThreedConfig.getConfigFile() + "]");
        }

        Path repoBaseUrl = new Path(repoBaseUrlName);
        return new InitData(seriesNamesWithYears, rtConfig, repoBaseUrl);
    }

    @Override
    public ArrayList<SeriesNamesWithYears> getSeriesNameWithYears() {
        ArrayList<SeriesNamesWithYears> seriesNamesWithYears = repos.getSeriesNamesWithYears();
        return seriesNamesWithYears;
    }

    @Override
    public Stats getJpgGenFinalStats(JobId jobId) {
        return jpgGen.getJob(jobId).getStats();
    }

    @Override
    public RootTreeId getVtcRootTreeId(SeriesKey seriesKey) {
        VtcService vtcService = new VtcService(repos);
        return vtcService.getVtcRootTreeId(seriesKey);
    }

    @Override
    public void setVtcRootTreeId(SeriesKey seriesKey, RootTreeId rootTreeId) {
        VtcService vtcService = new VtcService(repos);
        vtcService.setVtcCommitId(seriesKey, rootTreeId);
        log.info(seriesKey + " VTC set to [" + rootTreeId + "]");
    }

    @Override
    public RtConfig getRepoConfig() {
        return repos.getRtConfigHelper().read();
    }

    @Override
    public void saveRtConfig(RtConfig config) {
        repos.getRtConfigHelper().save(config);
    }

    @Override
    public CommitHistory getCommitHistory(SeriesKey seriesKey) throws RepoHasNoHeadException {
        SeriesRepo seriesRepo = repos.getSeriesRepo(seriesKey);
        SrcRepo srcRepo = seriesRepo.getSrcRepo();
        log.info("Building commit history on server..");
        final CommitHistory commitHistory = srcRepo.getHeadCommitHistory();
        log.info("Building commit history on server complete!");
        System.out.println("Serving commitHistory for [" + seriesKey + "]");
        return commitHistory;
    }

    @Override
    public void processCall(ClientOracle clientOracle, String payload, OutputStream stream) throws SerializationException {
        super.processCall(clientOracle, payload, stream);
    }

    @Override
    public CommitHistory tagCommit(SeriesKey seriesKey, String newTagName, CommitId commitId) {
        log.info("Tagging commit[" + commitId + "] with tag[" + newTagName + "]");
        SeriesRepo seriesRepo = repos.getSeriesRepo(seriesKey);
        SrcRepo srcRepo = seriesRepo.getSrcRepo();
        final ObjectId objectId = srcRepo.tagCommit(newTagName, commitId);
        final CommitHistory commitHistory = srcRepo.getCommitHistory(objectId);
        return commitHistory;
    }

    public CommitHistory addAllAndCommit(SeriesKey seriesKey, String commitMessage, String tag) {

        try {
            SeriesRepo seriesRepo = repos.getSeriesRepo(seriesKey);

            SrcRepo srcRepo = seriesRepo.getSrcRepo();

            if (isEmpty(commitMessage)) {
                commitMessage = System.currentTimeMillis() + "";
            }

            RevCommit revCommit = srcRepo.addAllAndCommit(commitMessage);


            BlinkCheckin.processBlinks(srcRepo.getGitRepo(), revCommit);

            ObjectId newCommitId = revCommit.getId();

            BlinkCheckin.processBlinks(srcRepo.getGitRepo(), revCommit);

            if (notEmpty(tag)) {
                srcRepo.tagCommit(tag, revCommit);
            }

            CommitHistory commitHistory = srcRepo.getCommitHistory(newCommitId, true);

            return commitHistory;
        } catch (Exception e) {
            e.printStackTrace();
            final String msg = "Error in ThreedAdminServlet.addAllAndCommit(" + seriesKey + "," + commitMessage + "," + tag + "). See server log for details.";
            log.error(msg,e);
            throw new RuntimeException(msg,e);
        }

    }

    @Override
    public void purgeRepoCache() {
        repos.purgeCache();
    }

    public JsonNode checkinFromTeamSite(HttpServletRequest request) throws Exception {
        SeriesKey seriesKey = getSeriesKey(request);

        final String commitMessage = request.getParameter("commitMessage");
        String tag = request.getParameter("edition");
        if (tag == null) {
            tag = request.getParameter("tag");
        }

        final CommitHistory commitHistory = addAllAndCommit(seriesKey, commitMessage, tag);

        ObjectNode o = f.objectNode();
        o.put("message", "Commit successful");
        o.put("newCommitId", commitHistory.getCommitId().getName());

        return o;

    }


//    public void tagCurrentVersion(HttpServletRequest request) throws Exception {
//        SeriesKey seriesKey = getSeriesKey(request);
//
//        String tagName = request.getParameter("tagName");
//        if (isEmpty(tagName)) throw new IllegalArgumentException("tagName is a required request parameter");
//
//        SeriesRepo seriesRepo = repos.getSeriesRepo(seriesKey);
//
//        SrcRepo srcRepo = seriesRepo.getSrcRepo();
//
//        srcRepo.tagCurrentVersion(tagName);
//
//
//    }


    @Override protected void doUnexpectedFailure(Throwable e) {
        log.error("Problem in RPC method", e);
        super.doUnexpectedFailure(e);
    }


}
