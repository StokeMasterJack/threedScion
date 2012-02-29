package com.tms.threed.threedAdmin.main.server;

import com.google.common.collect.ImmutableList;
import com.tms.threed.jpgGen.server.JobStatus;
import com.tms.threed.jpgGen.server.taskManager.EquivalentJobAlreadyRunningException;
import com.tms.threed.jpgGen.server.taskManager.JpgGeneratorService;
import com.tms.threed.jpgGen.server.taskManager.Master;
import com.tms.threed.jpgGen.shared.ExecutorStatus;
import com.tms.threed.jpgGen.shared.JobId;
import com.tms.threed.jpgGen.shared.JobState;
import com.tms.threed.jpgGen.shared.Stats;
import com.tms.threed.repo.server.Repos;
import com.tms.threed.threedCore.threedModel.shared.JpgWidth;
import com.tms.threed.threedCore.threedModel.shared.RootTreeId;
import com.tms.threed.threedCore.threedModel.shared.SeriesId;
import com.tms.threed.threedCore.threedModel.shared.SeriesKey;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.TextNode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import static com.tms.threed.util.date.shared.StringUtil.isEmpty;

/**
 * <repo-base>/threed-admin/threedAdminService.json?command=<command>&param1=value1&param2=value2
 *
 * commands:
 *
 *  startJpgJob         seriesName=sienna&seriesYear=2011&rootTreeId=6fa35e20173aebadec201fc7caa32ededd946d34&jpgWidth=wStd
 *  jpgQueueStatus      -- no extra params --
 *  jpgQueueDetails     jobId=1330477751704
 *  cancelJob           jobId=1330477751704
 *  removeJob           jobId=1330477751704
 *  removeTerminal      -- no extra params --
 */
public class RestJpgGenService {

    private final static Log log;

    static {
        log = LogFactory.getLog(RestJpgGenService.class);
    }

    private final JsonNodeFactory f = JsonNodeFactory.instance;

    private final Repos repos;
    private final JpgGeneratorService jpgGen;

    public RestJpgGenService(Repos repos) {
        this.repos = repos;
        this.jpgGen = new JpgGeneratorService(repos);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {

        String command = req.getParameter("command");

        try {
            JsonNode retVal = null;

            if (command.equals("startJpgJob")) {
                retVal = startJpgJob(req);
            } else if (command.equals("jpgQueueStatus")) {
                retVal = getJpgQueueStatus(req);
            } else if (command.equals("jpgQueueDetails")) {
                retVal = getJpgQueueDetails(req);
            } else if (command.equals("cancelJob")) {
                retVal = cancelJob(req);
            } else if (command.equals("removeJob")) {
                retVal = removeJob(req);
            } else if (command.equals("removeTerminal")) {
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


    private JsonNode getJpgQueueStatus(HttpServletRequest request) {
        Collection<Master> masterJobs = jpgGen.getMasterJobs();

        ArrayNode jsArray = f.arrayNode();

        for (Master job : masterJobs) {

            ObjectNode jsObject = f.objectNode();

            SeriesId seriesId = job.getSeriesId();
            putSeriesId(jsObject, seriesId);

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

    private JsonNode cancelJob(HttpServletRequest request) {
        String sJobId = request.getParameter("jobId");
        JobId jobId = new JobId(sJobId);
        log.warn("Cancelling jpg job: " + jobId);
        jpgGen.cancelJob(jobId);
        return f.textNode("OK");
    }

    private JsonNode removeJob(HttpServletRequest request) {
        String sJobId = request.getParameter("jobId");
        JobId jobId = new JobId(sJobId);
        jpgGen.removeJob(jobId);
        return f.textNode("OK");
    }


    private JsonNode removeTerminal(HttpServletRequest request) {
        jpgGen.removeTerminal();
        return f.textNode("OK");
    }

    private ArrayNode getJpgQueueDetails(HttpServletRequest request) {

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

    private ObjectNode startJpgJob(HttpServletRequest request) {
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

    private JpgWidth getJpgWidth(HttpServletRequest request) {
        String jpgWidth = request.getParameter("jpgWidth");
        return new JpgWidth(jpgWidth);
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

    public Stats getJpgGenFinalStats(JobId jobId) {
        return jpgGen.getJob(jobId).getStats();
    }

    public void destroy() {
        log.info("\t Shutting down JpgGenerator..");
        jpgGen.stopAndWait();
        log.info("\tJpgGenerator shutdown complete");
    }


}
